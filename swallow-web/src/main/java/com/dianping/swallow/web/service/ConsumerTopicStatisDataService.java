package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;

/**
*
* @author qiyin
*
*/
public interface ConsumerTopicStatisDataService {

	public boolean insert(ConsumerTopicStatsData statisData);

	public boolean update(ConsumerTopicStatsData statisData);

	public int deleteById(String id);

	public ConsumerTopicStatsData findById(String id);
	
	public ConsumerTopicStatsData findByTimeKey(long timeKey);
	
	public List<ConsumerTopicStatsData> findByTopic(String topicName);

}
