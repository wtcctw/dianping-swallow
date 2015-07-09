package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;

public interface ConsumerServerStatisDataService {

	public boolean insert(ConsumerServerStatsData statisData);

	public boolean update(ConsumerServerStatsData statisData);

	public int deleteById(String id);

	public ConsumerServerStatsData findById(String id);
	
	public ConsumerServerStatsData findByTimeKey(long timeKey);
	
}