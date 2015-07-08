package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.TopicStatisData;

public interface TopicStatisDataService {

	public boolean insert(TopicStatisData statisData);

	public boolean update(TopicStatisData statisData);

	public int deleteById(String id);

	public TopicStatisData findById(String id);
	
	public TopicStatisData findByTimeKey(long timeKey);
	
	public List<TopicStatisData> findByTopic(String topicName);

}
