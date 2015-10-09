package com.dianping.swallow.web.alarmer.storager;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.container.StatsDataContainer;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
@Component
public class ConsumerStatsDataStorager extends AbstractStatsDataStorager {

	@Autowired
	private StatsDataContainer statsDataContainer;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerServerStatsDataService serverStatsDataService;

	@Autowired
	private ConsumerTopicStatsDataService topicStatsDataService;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Autowired
	private ConsumerIpStatsDataService ipStatsDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}

	@Override
	protected void doStorage() {
		List<ConsumerServerStatsData> serverStatsDatas = consumerStatsDataWapper.getServerStatsDatas(lastTimeKey.get(),
				true);
		List<ConsumerIdStatsData> consumerIdStatsDatas = consumerStatsDataWapper.getConsumerIdStatsDatas(
				lastTimeKey.get(), true);
		ConsumerTopicStatsData topicStatsData = consumerStatsDataWapper.getTotalTopicStatsData(lastTimeKey.get());
		//List<ConsumerIpStatsData> ipStatsDatas = consumerStatsDataWapper.getIpStatsDatas(lastTimeKey.get(), false);
		doStorageServerStats(serverStatsDatas);
		doStorageTopicStats(topicStatsData);
		doStorageConsumerIdStats(consumerIdStatsDatas);
		//doStorageIpStats(ipStatsDatas);
	}

	private void doStorageServerStats(final List<ConsumerServerStatsData> serverStatsDatas) {
		logger.info("[doStorageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas == null) {
					return;
				}
				boolean isFirstTime = true;
				for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
					if (isFirstTime) {
						lastTimeKey.set(serverStatsData.getTimeKey());
						isFirstTime = false;
					}
					serverStatsDataService.insert(serverStatsData);
				}
			}
		});

	}

	private void doStorageTopicStats(final ConsumerTopicStatsData topicStatsData) {
		logger.info("[doStorageTopicStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageTopicStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatsData == null) {
					return;
				}
				topicStatsDataService.insert(topicStatsData);
			}
		});

	}

	private void doStorageConsumerIdStats(final List<ConsumerIdStatsData> consumerIdStatsDatas) {
		logger.info("[doStorageConsumerIdStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageConsumerIdStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (consumerIdStatsDatas == null) {
					return;
				}

				statsDataContainer.setConsumerIdTotalRatio(consumerIdStatsDatas);
				final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(consumerIdStatsDatas.size());
				for (final ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
					executor.submit(new Runnable() {

						@Override
						public void run() {
							try {
								consumerIdStatsDataService.insert(consumerIdStatsData);
							} catch (Throwable t) {
								logger.error("[doStorageConsumerIdStats] insert {}", consumerIdStatsData, t);
							} finally {
								downLatch.countDown();
							}
						}

					});
				}
				CountDownLatchUtil.await(downLatch);
			}
		});
	}

	private void doStorageIpStats(final List<ConsumerIpStatsData> ipStatsDatas) {
		logger.info("[doStorageIpStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageIpStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (ipStatsDatas == null) {
					return;
				}

				final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(ipStatsDatas.size());

				for (final ConsumerIpStatsData ipStatsData : ipStatsDatas) {
					try {

						executor.submit(new Runnable() {
							@Override
							public void run() {
								ipStatsDataService.insert(ipStatsData);
							}
						});
					} catch (Throwable t) {
						logger.error("[doStorageIpStats] insert {}", ipStatsData, t);
					} finally {
						downLatch.countDown();
					}
				}
				CountDownLatchUtil.await(downLatch);
			}
		});
	}
}
