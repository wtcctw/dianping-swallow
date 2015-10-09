package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerIpStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:20:57
 */
public interface ProducerIpStatsDataDao {

	boolean insert(ProducerIpStatsData ipStatsData);

	List<ProducerIpStatsData> find(String topicName, String ip, long startKey, long endKey);

}
