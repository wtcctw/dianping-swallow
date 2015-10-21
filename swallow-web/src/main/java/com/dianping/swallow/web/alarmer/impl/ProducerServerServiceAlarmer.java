package com.dianping.swallow.web.alarmer.impl;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.server.ProducerServer;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午4:54:37
 */
@Component
public class ProducerServerServiceAlarmer extends AbstractServiceAlarmer {

	@Override
	public void doAlarm() {
		doCheck();
	}

	public void doCheck() {
		Map<String, ProducerServer> producerServers = serverContainer.getProducerServers();
		if (producerServers == null || producerServers.isEmpty()) {
			logger.error("[doCheck] producerServers is empty.");
			return;
		}
		final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(producerServers.size());
		for (Map.Entry<String, ProducerServer> serverEntry : producerServers.entrySet()) {
			try {
				final ProducerServer producerServer = serverEntry.getValue();
				taskManager.submit(new Runnable() {
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
			} finally {
				downLatch.countDown();
			}
		}
		CountDownLatchUtil.await(downLatch);
	}

	public void doServerService(ProducerServer producerServer) {
		try {
			producerServer.checkService();
		} catch (Throwable t) {
			logger.error("[run] server {} checkService error.", producerServer);
		}
	}

	public void doDataSend(ProducerServer producerServer) {
		super.doDataSend(producerServer, true);
	}
}
