package com.dianping.swallow.web.dao.stats;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

public interface ProducerTopicStatsDataDao {

	public boolean insert(ProducerTopicStatsData statisData);

	public boolean update(ProducerTopicStatsData statisData);

	public int deleteById(String id);

	public ProducerTopicStatsData findById(String id);

	public List<ProducerTopicStatsData> findByTopic(String topicName);

	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);
}
