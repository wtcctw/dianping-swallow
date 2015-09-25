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
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerIpStatsDataService;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;
import com.dianping.swallow.web.util.CountDownLatchUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
@Component
public class ProducerStatsDataStorager extends AbstractStatsDataStorager {

	@Autowired
	private StatsDataContainer statsDataContainer;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService topicStatsDataService;

	@Autowired
	private ProducerIpStatsDataService ipStatsDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}

	@Override
	protected void doStorage() {
		List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(lastTimeKey.get(),
				true);
		List<ProducerTopicStatsData> topicStatsDatas = producerStatsDataWapper.getTopicStatsDatas(lastTimeKey.get(),
				true);
		List<ProducerIpStatsData> ipStatsDatas = producerStatsDataWapper.getIpStatsDatas(lastTimeKey.get(), false);
		doStorageServerStats(serverStatsDatas);
		doStorageTopicStats(topicStatsDatas);
		doStorageIpStats(ipStatsDatas);
	}

	private void doStorageServerStats(final List<ProducerServerStatsData> serverStatsDatas) {
		logger.info("[doStorageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas != null) {
					boolean isFirstTime = true;
					for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
						if (isFirstTime) {
							lastTimeKey.set(serverStatsData.getTimeKey());
							isFirstTime = false;
						}
						serverStatsDataService.insert(serverStatsData);
					}
				}
			}
		});
	}

	private void doStorageTopicStats(final List<ProducerTopicStatsData> topicStatsDatas) {
		logger.info("[doStorageTopicStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageTopicStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatsDatas != null) {

					statsDataContainer.setProducerTopicTotalRatio(topicStatsDatas);

					final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(topicStatsDatas.size());
					for (final ProducerTopicStatsData producerTopicStatisData : topicStatsDatas) {
						executor.submit(new Runnable() {

							@Override
							public void run() {
								topicStatsDataService.insert(producerTopicStatisData);
								downLatch.countDown();
							}

						});
					}
					CountDownLatchUtil.await(downLatch);
				}
			}
		});
	}

	private void doStorageIpStats(final List<ProducerIpStatsData> ipStatsDatas) {
		logger.info("[doStorageIpStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageIpStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (ipStatsDatas != null) {

					final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(ipStatsDatas.size());

					for (final ProducerIpStatsData ipStatsData : ipStatsDatas) {
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
			}
		});
	}
}
