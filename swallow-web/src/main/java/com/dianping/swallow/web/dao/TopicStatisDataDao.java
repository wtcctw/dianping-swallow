package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.TopicStatsData;

/**
*
* @author qiyin
*
*/
public interface TopicStatisDataDao {

	public boolean insert(TopicStatsData statisData);

	public boolean update(TopicStatsData statisData);

	public int deleteById(String id);

	public TopicStatsData findById(String id);
	
	public TopicStatsData findByTimeKey(long timeKey);
	
	public List<TopicStatsData> findByTopic(String topicName);

}
