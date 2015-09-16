package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.container.StatsDataContainer;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;

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
		doStorageServerStats(serverStatsDatas);
		doStorageTopicStats(topicStatsData);
		doStorageConsumerIdStats(consumerIdStatsDatas);
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

				for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
					consumerIdStatsDataService.insert(consumerIdStatsData);
				}

			}
		});
	}
}
