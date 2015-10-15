package com.dianping.swallow.web.alarmer.storager;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.container.StatsDataContainer;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午7:45:26
 */
public class ConsumerIdStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private StatsDataContainer statsDataContainer;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Override
	protected void doStorage() {
		logger.info("[doStorageConsumerIdStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageConsumerIdStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				doStorageConsumerIdStats();
			}
		});
	}

	private void doStorageConsumerIdStats() {
		Set<String> topicNames = consumerStatsDataWapper.getTopics(false);
		final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(topicNames.size());
		for (String topicName : topicNames) {
			final List<ConsumerIdStatsData> consumerIdStatsDatas = consumerStatsDataWapper.getConsumerIdStatsDatas(
					topicName, getLastTimeKey(), true);
			if (consumerIdStatsDatas == null) {
				return;
			}
			statsDataContainer.setConsumerIdTotalRatio(consumerIdStatsDatas);
			taskManager.submit(new Runnable() {
				@Override
				public void run() {
					try {
						consumerIdStatsDataService.insert(consumerIdStatsDatas);
					} catch (Throwable t) {
						logger.error("[doStorageConsumerIdStats] insert consumerIdStatsDatas", t);
					} finally {
						downLatch.countDown();
					}
				}

			});
			CountDownLatchUtil.await(downLatch);
		}

	}
}
