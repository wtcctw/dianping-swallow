package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:38:54
 */
public interface ConsumerIpStatsDataDao {

	boolean insert(ConsumerIpStatsData ipStatsData);

	boolean insert(List<ConsumerIpStatsData> ipStatsDatas);

	boolean removeLessThanTimeKey(long timeKey);

	List<ConsumerIpStatsData> find(String topicName, String consumerId, String ip, long startKey, long endKey);
}
