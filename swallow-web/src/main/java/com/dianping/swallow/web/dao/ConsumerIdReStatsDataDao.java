package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.monitor.model.ConsumerIdReStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午12:28:06
 */
public interface ConsumerIdReStatsDataDao {

	boolean insert(ConsumerIdReStatsData consumerIdReStatsData);

	boolean update(ConsumerIdReStatsData consumerIdReStatsData);

	List<ConsumerIdReStatsData> findByPage(String topicName, String consumerId,int limit,int offset);

	List<ConsumerIdReStatsData> findByTimeKey(long fromTimeKey, long toTimeKey);
}
