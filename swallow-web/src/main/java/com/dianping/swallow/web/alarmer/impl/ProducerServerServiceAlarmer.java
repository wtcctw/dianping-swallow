package com.dianping.swallow.web.alarmer.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.server.ProducerServer;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午4:54:37
 */
@Component
public class ProducerServerServiceAlarmer extends AbstractServiceAlamer1 {

	private Map<String, ProducerServer> producerServers = new ConcurrentHashMap<String, ProducerServer>();

	public void doAlarm() {
		updateServers();
		doCheck();
	}

	public void doCheck() {
		final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(producerServers.size());
		for (Map.Entry<String, ProducerServer> serverEntry : producerServers.entrySet()) {
			try {
				final ProducerServer producerServer = serverEntry.getValue();
				threadManager.submit(new Runnable() {
					@Override
					public void run() {
						try {
							doServerService(producerServer);
							doDataSend(producerServer);
						} catch (Throwable t) {
							logger.error("[run] server {} doCheck error.", producerServer);
						} finally {
							downLatch.countDown();
						}
					}
				});
			} catch (Throwable t) {
				logger.error("[submit] executor thread submit error.", t);
			}
		}
		CountDownLatchUtil.await(downLatch);
	}

	public void updateServers() {
		List<ProducerServerResource> serverResources = resourceContainer.findProducerServerResources(false);
		removeServer(serverResources);
		addServer(serverResources);
	}

	private void removeServer(List<ProducerServerResource> serverResources) {
		Iterator<Entry<String, ProducerServer>> serverIterator = producerServers.entrySet().iterator();
		while (serverIterator.hasNext()) {
			Entry<String, ProducerServer> entryServer = serverIterator.next();
			String ip = entryServer.getKey();
			boolean isHasElement = false;
			if (serverResources == null) {
				isHasElement = false;
			} else {
				for (ProducerServerResource serverResource : serverResources) {
					if (serverResource.isAlarm()) {
						if (StringUtils.equals(ip, serverResource.getIp())) {
							isHasElement = true;
							break;
						}
					}
				}
			}
			if (!isHasElement) {
				serverIterator.remove();
			}
		}
	}

	private void addServer(List<ProducerServerResource> serverResources) {
		for (ProducerServerResource serverResource : serverResources) {
			String ip = serverResource.getIp();
			if (StringUtils.isBlank(ip) || !serverResource.isAlarm()) {
				continue;
			}
			if (!producerServers.containsKey(ip)) {
				producerServers.put(ip, serverFactory.createProducerServer(ip));
			}
		}
	}

	public void doServerService(final ProducerServer producerServer) {
		try {
			producerServer.checkService();
		} catch (Throwable t) {
			logger.error("[run] server {} checkService error.", producerServer);
		}
	}

	public void doDataSend(final ProducerServer producerServer) {
		super.doDataSend(producerServer, producerServer.getIp(), true);
	}
}
