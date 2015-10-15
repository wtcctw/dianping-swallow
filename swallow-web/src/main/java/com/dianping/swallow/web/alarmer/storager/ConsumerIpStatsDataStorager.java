package com.dianping.swallow.web.alarmer.storager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:12:29
 */
public class ConsumerIpStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private ConsumerIpStatsDataService ipStatsDataService;

	@Override
	protected void doStorage() {
		logger.info("[doStorageIpStats].");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageIpStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				doStorageIpStats();
			}
		});

	}

	private void doStorageIpStats() {
		Set<String> topicNames = consumerStatsDataWapper.getTopics(false);
		for (String topicName : topicNames) {
			Set<String> consumerIds = consumerStatsDataWapper.getConsumerIds(topicName, false);
			final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(consumerIds.size());
			for (String consumerId : consumerIds) {
				final List<ConsumerIpStatsData> ipStatsDatas = consumerStatsDataWapper.getIpStatsDatas(topicName,
						consumerId, getLastTimeKey(), false);
				if (ipStatsDatas == null) {
					downLatch.countDown();
					continue;
				}
				try {
					executor.submit(new Runnable() {
						@Override
						public void run() {
							try {
								ipStatsDataService.insert(ipStatsDatas);
							} catch (Throwable t) {
								logger.error("[doStorageIpStats] insert ipstatsDatas.", t);
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

}
