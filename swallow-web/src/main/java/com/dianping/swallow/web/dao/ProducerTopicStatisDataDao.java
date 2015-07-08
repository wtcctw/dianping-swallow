package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerTopicStatisData;

public interface ProducerTopicStatisDataDao {

	public boolean insert(ProducerTopicStatisData statisData);

	public boolean update(ProducerTopicStatisData statisData);

	public int deleteById(String id);

	public ProducerTopicStatisData findById(String id);
	
	public ProducerTopicStatisData findByTimeKey(long timeKey);
	
	public List<ProducerTopicStatisData> findByTopic(String topicName);

}
