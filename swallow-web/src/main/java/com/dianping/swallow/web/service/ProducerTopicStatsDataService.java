package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:39:30
 */
public interface ProducerTopicStatsDataService {

	boolean insert(ProducerTopicStatsData topicStatsData);

	boolean update(ProducerTopicStatsData topicStatsData);

	int deleteById(String id);

	ProducerTopicStatsData findById(String id);

	List<ProducerTopicStatsData> findByTopic(String topicName);

	List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);
}
