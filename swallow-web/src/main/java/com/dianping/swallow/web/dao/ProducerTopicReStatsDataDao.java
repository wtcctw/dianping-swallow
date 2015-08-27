package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.monitor.model.ProducerTopicReStatsData;
/**
 * 
 * @author qiyin
 *
 * 2015年8月27日 下午12:33:00
 */
public interface ProducerTopicReStatsDataDao {
	
	boolean insert(ProducerTopicReStatsData topicReStatsData);

	boolean update(ProducerTopicReStatsData topicReStatsData);

	List<ProducerTopicReStatsData> findByPage(String topicName,int limit,int offset);

	List<ProducerTopicReStatsData> findByTimeKey(long fromTimeKey, long toTimeKey);
}
