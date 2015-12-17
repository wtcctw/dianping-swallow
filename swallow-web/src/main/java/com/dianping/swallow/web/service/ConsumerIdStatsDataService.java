package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:40:22
 */
public interface ConsumerIdStatsDataService {

	boolean insert(ConsumerIdStatsData consumerIdStatsData);
	
	boolean insert(List<ConsumerIdStatsData> consumerIdStatsDatas);

	boolean removeLessThanTimeKey(long timeKey);

	List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId, int offset, int limit);

	List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey);

	List<ConsumerIdStatsData> findSectionData(String topicName, long startKey, long endKey);

	Map<String, StatsDataMapPair> findSectionQpsData(String topicName, long startKey, long endKey);

	Map<String, StatsDataMapPair> findSectionDelayData(String topicName, long startKey, long endKey);

	Map<String, NavigableMap<Long, Long>> findSectionAccuData(String topicName, long startKey, long endKey);

	ConsumerIdStatsData findOneByTopicAndTimeAndConsumerId(String topicName, String consumerId, long startKey,
			long endKey, boolean isGt);

	ConsumerIdStatsData findOldestData();
}
