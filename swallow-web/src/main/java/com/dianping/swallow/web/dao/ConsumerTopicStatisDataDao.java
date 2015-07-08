package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerTopicStatisData;

public interface ConsumerTopicStatisDataDao {

	public boolean insert(ConsumerTopicStatisData statisData);

	public boolean update(ConsumerTopicStatisData statisData);

	public int deleteById(String id);

	public ConsumerTopicStatisData findById(String id);
	
	public ConsumerTopicStatisData findByTimeKey(long timeKey);
	
	public List<ConsumerTopicStatisData> findByTopic(String topicName);

}
