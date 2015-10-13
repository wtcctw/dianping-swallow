package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月24日 上午11:39:24
 */
public interface ConsumerTopicStatsDataDao {

	boolean insert(ConsumerTopicStatsData topicStatsData);
	
	boolean insert(List<ConsumerTopicStatsData> topicStatsDatas);

	List<ConsumerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);

}
