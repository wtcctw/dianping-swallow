package com.dianping.swallow.web.service.stats;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:39:30
 */
public interface ProducerTopicStatsDataService {
	
	public boolean insert(ProducerTopicStatsData topicStatsData);

	public boolean update(ProducerTopicStatsData topicStatsData);

	public int deleteById(String id);

	public ProducerTopicStatsData findById(String id);

	public List<ProducerTopicStatsData> findByTopic(String topicName);

	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey);
}
