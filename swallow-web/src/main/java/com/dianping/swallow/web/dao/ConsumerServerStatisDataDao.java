package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerServerStatisData;

public interface ConsumerServerStatisDataDao {

	public boolean insert(ConsumerServerStatisData statisData);

	public boolean update(ConsumerServerStatisData statisData);

	public int deleteById(String id);

	public ConsumerServerStatisData findById(String id);
	
	public ConsumerServerStatisData findByTimeKey(String timeKey);
	
	public ConsumerServerStatisData findByTopic(String topicName);

	public List<ConsumerServerStatisData> findAll();
}
