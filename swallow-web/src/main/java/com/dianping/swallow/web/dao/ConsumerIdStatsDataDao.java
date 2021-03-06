package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:27
 */
public interface ConsumerIdStatsDataDao {

	boolean insert(ConsumerIdStatsData consumerIdStatsData);
	
	boolean insert(List<ConsumerIdStatsData> consumerIdStatsDatas);

	boolean removeLessThanTimeKey(long timeKey);

	List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId, int offset, int limit);

	ConsumerIdStatsData findOneByTopicAndTimeAndConsumerId(String topicName, String consumerId, long startKey,
			long endKey, boolean isGt);

	List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey);

	List<ConsumerIdStatsData> findSectionData(String topicName, long startKey, long endKey);

	ConsumerIdStatsData findOldestData();
}
