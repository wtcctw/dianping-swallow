package com.dianping.swallow.web.alarmer.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer.ConsumerServerResourcePair;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.model.server.ConsumerHAServer;
import com.dianping.swallow.web.model.server.ConsumerServer;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午4:54:31
 */
@Component
public class ConsumerServerServiceAlarmer extends AbstractServiceAlarmer {

	private Map<String, ConsumerHAServer> consumerHAServers = new ConcurrentHashMap<String, ConsumerHAServer>();

	@Override
	public void doAlarm() {
		updateServers();
		doCheck();
	}

	public void doCheck() {
		final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(consumerHAServers.size());
		for (Map.Entry<String, ConsumerHAServer> serverEntry : consumerHAServers.entrySet()) {
			try {
				final ConsumerHAServer consumerHAServer = serverEntry.getValue();
				taskManager.submit(new Runnable() {
					@Override
					public void run() {
						try {
							doServerPort(consumerHAServer);
							doDataSend(consumerHAServer);
							doServerService(consumerHAServer);
						} catch (Throwable t) {
							logger.error("[run] server {} doCheck error.", consumerHAServer);
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
		List<ConsumerServerResourcePair> serverResourcePairs = resourceContainer.findConsumerServerResourcePairs();
		removeServer(serverResourcePairs);
		addServer(serverResourcePairs);
	}

	public void doServerPort(final ConsumerHAServer consumerHAServer) {
		try {
			consumerHAServer.checkPort();
		} catch (Throwable t) {
			logger.error("[run] server {} checkPort error.", consumerHAServer);
		}
	}

	public void doDataSend(final ConsumerHAServer consumerHAServer) {
		super.doDataSend(consumerHAServer, consumerHAServer.getMasterServer().getIp(), false);
	}

	public void doServerService(final ConsumerHAServer consumerHAServer) {
		try {
			consumerHAServer.checkService();
		} catch (Throwable t) {
			logger.error("[run] server {} checkService error.", consumerHAServer);
		}
	}

	private void removeServer(List<ConsumerServerResourcePair> serverResourcePairs) {
		Iterator<Entry<String, ConsumerHAServer>> serverIterator = consumerHAServers.entrySet().iterator();
		while (serverIterator.hasNext()) {
			Entry<String, ConsumerHAServer> entryServer = serverIterator.next();
			String ip = entryServer.getKey();
			boolean isHasElement = false;
			if (serverResourcePairs == null) {
				isHasElement = false;
			} else {
				for (ConsumerServerResourcePair serverResourcePair : serverResourcePairs) {
					ConsumerServerResource serverResource = serverResourcePair.getMasterResource();
					if (serverResource.getType() == ServerType.MASTER && serverResource.isAlarm()) {
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

	private void addServer(List<ConsumerServerResourcePair> serverResourcePairs) {
		if (serverResourcePairs == null) {
			return;
		}
		for (ConsumerServerResourcePair serverResourcePair : serverResourcePairs) {
			ConsumerServerResource masterResource = serverResourcePair.getMasterResource();
			ConsumerServerResource slaveResource = serverResourcePair.getSlaveResource();
			String masterIp = masterResource.getIp();
			int masterPort = masterResource.getPort();
			String slaveIp = slaveResource.getIp();
			int slavePort = slaveResource.getPort();
			if (StringUtils.isBlank(masterIp) || !masterResource.isAlarm()) {
				continue;
			}
			ConsumerServer masterServer = serverFactory.createConsumerServer(masterIp, masterPort, true);
			ConsumerServer slaveServer = serverFactory.createConsumerServer(slaveIp, slavePort, false);
			if (!consumerHAServers.containsKey(masterIp)) {
				consumerHAServers.put(masterIp, serverFactory.createConsumerHAServer(masterServer, slaveServer));
			}
		}
	}
}
