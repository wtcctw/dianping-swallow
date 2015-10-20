package com.dianping.swallow.web.alarmer.impl;

import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.server.ConsumerHAServer;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 下午4:54:31
 */
@Component
public class ConsumerServerServiceAlarmer extends AbstractServiceAlarmer {

	@Override
	public void doAlarm() {
		doCheck();
	}

	public void doCheck() {
		Map<String, ConsumerHAServer> consumerHAServers = serverContainer.getConsumerHAServers();
		if (consumerHAServers == null || consumerHAServers.isEmpty()) {
			logger.error("[doCheck] consumerHAServers is empty.");
			return;
		}
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
			} finally {
				downLatch.countDown();
			}
		}
		CountDownLatchUtil.await(downLatch);
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

}
