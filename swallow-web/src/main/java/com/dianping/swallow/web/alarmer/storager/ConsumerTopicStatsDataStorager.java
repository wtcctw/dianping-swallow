package com.dianping.swallow.web.alarmer.storager;

import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;

public class ConsumerTopicStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Override
	protected void doStorage() {
		ConsumerTopicStatsData topicStatsData = consumerStatsDataWapper.getTotalTopicStatsData(lastTimeKey.get());

	}

}
