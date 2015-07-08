package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.statis.ProducerServerStatisData;

public interface ProducerServerStatisDataService {
	
	public boolean insert(ProducerServerStatisData statisData);

	public boolean update(ProducerServerStatisData statisData);

	public int deleteById(String id);

	public ProducerServerStatisData findById(String id);
	
	public ProducerServerStatisData findByTimeKey(long timeKey);
	
}
