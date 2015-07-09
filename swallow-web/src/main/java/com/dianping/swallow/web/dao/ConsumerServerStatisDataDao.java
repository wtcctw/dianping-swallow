package com.dianping.swallow.web.dao;


import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;

public interface ConsumerServerStatisDataDao {

	public boolean insert(ConsumerServerStatsData statisData);

	public boolean update(ConsumerServerStatsData statisData);

	public int deleteById(String id);

	public ConsumerServerStatsData findById(String id);
	
	public ConsumerServerStatsData findByTimeKey(long timeKey);
	
}