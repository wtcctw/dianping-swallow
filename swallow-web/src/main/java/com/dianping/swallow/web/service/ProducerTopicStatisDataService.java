package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;

public interface ProducerTopicStatisDataService {

	public boolean insert(ProducerTopicStatsData statisData);

	public boolean update(ProducerTopicStatsData statisData);

	public int deleteById(String id);

	public ProducerTopicStatsData findById(String id);
	
	public ProducerTopicStatsData findByTimeKey(long timeKey);
	
	public List<ProducerTopicStatsData> findByTopic(String topicName);

	public List<ProducerTopicStatsData> findSectionData(long startKey, long endKey);
	
}
