package com.dianping.swallow.web.alarmer.storager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.service.ProducerIpStatsDataService;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:12:05
 */
public class ProducerIpStatsDataStorager extends AbstractProducerStatsDataStorager {

	@Autowired
	private ProducerIpStatsDataService ipStatsDataService;

	@Override
	protected void doStorage() {
		doStorageIpStats();
	}

	private void doStorageIpStats() {
		logger.info("[doStorageIpStats].");
		Set<String> topicNames = producerStatsDataWapper.getTopics(false);
		if (topicNames == null || topicNames.isEmpty()) {
			return;
		}
		final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(topicNames.size());
		for (String topicName : topicNames) {
			final List<ProducerIpStatsData> ipStatsDatas = producerStatsDataWapper.getIpStatsDatas(topicName,
					getLastTimeKey(), false);
			if (ipStatsDatas == null) {
				downLatch.countDown();
				continue;
			}
			try {
				taskManager.submit(new Runnable() {
					@Override
					public void run() {
						try {
							ipStatsDataService.insert(ipStatsDatas);
						} catch (Throwable t) {
							logger.error("[doStorageIpStats] insert ipStatsDatas", t);
						} finally {
							downLatch.countDown();
						}
					}
				});
			} catch (Throwable t) {
				logger.error("[doStorageIpStats] executor submit error.", t);
			}
		}
		CountDownLatchUtil.await(downLatch);
	}

}
