package com.dianping.swallow.web.dao.stats;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;

public interface ConsumerServerStatsDataDao {

	public boolean insert(ConsumerServerStatsData statisData);

	public boolean update(ConsumerServerStatsData statisData);

	public int deleteById(String id);

	public ConsumerServerStatsData findById(String id);

	public ConsumerServerStatsData findByTimeKey(String ip, long timeKey);

	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey);

}
