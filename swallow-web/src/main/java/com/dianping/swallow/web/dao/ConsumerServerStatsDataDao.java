package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:22
 */
public interface ConsumerServerStatsDataDao {

	boolean insert(ConsumerServerStatsData serverStatsData);

	boolean update(ConsumerServerStatsData serverStatsData);

	int deleteById(String id);

	ConsumerServerStatsData findById(String id);

	ConsumerServerStatsData findByTimeKey(String ip, long timeKey);

	List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey);

}
