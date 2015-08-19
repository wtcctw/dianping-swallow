package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:40:22
 */
public interface ConsumerIdStatsDataService {

	boolean insert(ConsumerIdStatsData consumerIdStatsData);

	boolean update(ConsumerIdStatsData consumerIdStatsData);

	int deleteById(String id);

	ConsumerIdStatsData findById(String id);

	List<ConsumerIdStatsData> findByTimeKey(long timeKey);

	List<ConsumerIdStatsData> findByTopic(String topicName);

	List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey);

	List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId);

	List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId);

	List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey);
}
