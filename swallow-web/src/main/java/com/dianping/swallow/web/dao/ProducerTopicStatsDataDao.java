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
	
	boolean insert(List<ProducerTopicStatsData> topicStatsDatas);

	ProducerTopicStatsData findOneByTopicAndTime(String topicName, long startKey, long endKey, boolean isGt);

	List<ProducerTopicStatsData> findByTopic(String topicName, int offset, int limit);

	List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);

}
