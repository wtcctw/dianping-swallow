package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.statis.ConsumerServerStatisData;

public interface ConsumerServerStatisDataService {

	public boolean insert(ConsumerServerStatisData statisData);

	public boolean update(ConsumerServerStatisData statisData);

	public int deleteById(String id);

	public ConsumerServerStatisData findById(String id);
	
	public ConsumerServerStatisData findByTimeKey(long timeKey);
	
}