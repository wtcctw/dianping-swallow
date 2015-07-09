package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;

public interface ConsumerTopicStatisDataDao {

	public boolean insert(ConsumerTopicStatsData statisData);

	public boolean update(ConsumerTopicStatsData statisData);

	public int deleteById(String id);

	public ConsumerTopicStatsData findById(String id);
	
	public ConsumerTopicStatsData findByTimeKey(long timeKey);
	
	public List<ConsumerTopicStatsData> findByTopic(String topicName);

}
