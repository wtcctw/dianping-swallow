package com.dianping.swallow.web.alarmer.storager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午7:32:57
 */
@Component
public class ConsumerTopicStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private ConsumerTopicStatsDataService topicStatsDataService;

	@Override
	protected void doStorage() {
		doStorageTopicStats();
	}

	private void doStorageTopicStats() {
		logger.info("[doStorageTopicStats].");
		ConsumerTopicStatsData topicStatsData = consumerStatsDataWapper.getTotalTopicStatsData(getLastTimeKey());
		if (topicStatsData != null) {
			topicStatsDataService.insert(topicStatsData);
		}
	}

}
