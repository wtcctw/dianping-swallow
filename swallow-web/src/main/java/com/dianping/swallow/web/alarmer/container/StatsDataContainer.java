package com.dianping.swallow.web.alarmer.container;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午8:03:20
 */
public interface StatsDataContainer {

	void setConsumerIdTotalRatio(List<ConsumerIdStatsData> consumerIdStatsDatas);

	void setProducerTopicTotalRatio(List<ProducerTopicStatsData> topicStatsDatas);

}
