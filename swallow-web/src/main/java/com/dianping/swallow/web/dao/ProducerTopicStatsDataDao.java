package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:07
 */
public interface ProducerTopicStatsDataDao {

	boolean insert(ProducerTopicStatsData topicStatsData);

	boolean update(ProducerTopicStatsData topicStatsData);

	int deleteById(String id);

	ProducerTopicStatsData findById(String id);

	List<ProducerTopicStatsData> findByTopic(String topicName);

	ProducerTopicStatsData findOneByTopicAndTime(String topicName, long timeKey, boolean isGt);

	List<ProducerTopicStatsData> findByTopic(String topicName, int offset, int limit);

	List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);

}
