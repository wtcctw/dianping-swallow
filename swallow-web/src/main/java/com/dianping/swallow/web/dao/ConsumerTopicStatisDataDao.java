package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;

/**
 *
 * @author qiyin
 *
 */
public interface ConsumerTopicStatisDataDao {

	boolean insert(ConsumerTopicStatsData statisData);

	boolean update(ConsumerTopicStatsData statisData);

	int deleteById(String id);

	ConsumerTopicStatsData findById(String id);

	ConsumerTopicStatsData findByTimeKey(long timeKey);

	List<ConsumerTopicStatsData> findByTopic(String topicName);

	List<ConsumerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);

}
