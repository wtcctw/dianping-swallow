package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerIdStatisData;

public interface ConsumerIdStatisDataDao {

	public boolean insert(ConsumerIdStatisData statisData);

	public boolean update(ConsumerIdStatisData statisData);

	public int deleteById(String id);

	public ConsumerIdStatisData findById(String id);

	public List<ConsumerIdStatisData> findByTimeKey(long timeKey);

	public List<ConsumerIdStatisData> findByTopic(String topicName);

	public List<ConsumerIdStatisData> findByTopicAndTime(String topicName, long timeKey);

	public List<ConsumerIdStatisData> findByTopicAndConsumerId(String topicName, String consumerId);

	public List<ConsumerIdStatisData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId);

}
