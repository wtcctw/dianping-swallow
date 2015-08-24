package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;

/**
 * 
 * @author qiyin
 *
 *         2015年8月24日 下午12:59:51
 */
public interface ConsumerTopicStatsDataService {
	
	boolean insert(ConsumerTopicStatsData topicStatsData);

	List<ConsumerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);
	
	StatsDataMapPair findSectionDelayData(String topicName, long startKey, long endKey);
	
	StatsDataMapPair findSectionQpsData(String topicName, long startKey, long endKey);
	
}
