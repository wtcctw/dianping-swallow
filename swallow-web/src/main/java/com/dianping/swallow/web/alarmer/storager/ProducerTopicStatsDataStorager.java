package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.alarmer.container.StatsDataContainer;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:11:46
 */
@Component
public class ProducerTopicStatsDataStorager extends AbstractProducerStatsDataStorager {

	@Autowired
	private StatsDataContainer statsDataContainer;

	@Autowired
	private ProducerTopicStatsDataService topicStatsDataService;

	@Override
	protected void doStorage() {
		doStorageTopicStats();
	}

	private void doStorageTopicStats() {
		logger.info("[doStorageTopicStats] {}.");
		final List<ProducerTopicStatsData> topicStatsDatas = producerStatsDataWapper.getTopicStatsDatas(
				getLastTimeKey(), true);
		if (topicStatsDatas != null) {
			statsDataContainer.setProducerTopicTotalRatio(topicStatsDatas);
			topicStatsDataService.insert(topicStatsDatas);
		}
	}
}
