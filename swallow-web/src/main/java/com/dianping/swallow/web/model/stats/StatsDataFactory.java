package com.dianping.swallow.web.model.stats;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 下午5:32:23
 */
public interface StatsDataFactory {

	ProducerTopicStatsData createTopicStatsData();

	ProducerServerStatsData createProducerServerStatsData();

	ConsumerServerStatsData createConsumerServerStatsData();

	ConsumerIdStatsData createConsumerIdStatsData();

	ConsumerTopicStatsData createConsumerTopicStatsData();

	ProducerIpStatsData createProducerIpStatsData();

	ConsumerIpStatsData createConsumerIpStatsData();
}
