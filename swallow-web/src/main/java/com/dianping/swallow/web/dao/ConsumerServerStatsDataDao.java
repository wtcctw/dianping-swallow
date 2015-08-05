package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:38:22
 */
public interface ConsumerServerStatsDataDao {

	public boolean insert(ConsumerServerStatsData serverStatsData);

	public boolean update(ConsumerServerStatsData serverStatsData);

	public int deleteById(String id);

	public ConsumerServerStatsData findById(String id);

	public ConsumerServerStatsData findByTimeKey(String ip, long timeKey);

	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey);

}
