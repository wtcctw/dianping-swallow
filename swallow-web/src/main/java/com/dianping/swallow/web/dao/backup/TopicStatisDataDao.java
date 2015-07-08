package com.dianping.swallow.web.dao.backup;

import java.util.List;

import com.dianping.swallow.web.model.statis.backup.TopicStatisData;

public interface TopicStatisDataDao {

	public boolean insert(TopicStatisData statisData);

	public boolean update(TopicStatisData statisData);

	public int deleteById(String id);

	public TopicStatisData findById(String id);
	
	public TopicStatisData findByTimeKey(String timeKey);
	
	public TopicStatisData findByTopic(String topicName);

	public List<TopicStatisData> findAll();
}
