package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:38:27
 */
public interface ConsumerIdStatsDataDao {
	
	public boolean insert(ConsumerIdStatsData consumerIdStatsData);

	public boolean update(ConsumerIdStatsData consumerIdstatsData);

	public int deleteById(String id);

	public ConsumerIdStatsData findById(String id);

	public List<ConsumerIdStatsData> findByTimeKey(long timeKey);

	public List<ConsumerIdStatsData> findByTopic(String topicName);

	public List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey);

	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId);

	public List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId);

	List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey);

}
