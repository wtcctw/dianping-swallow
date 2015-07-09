package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;

public interface ProducerServerStatisDataService {
	
	public boolean insert(ProducerServerStatsData statisData);

	public boolean update(ProducerServerStatsData statisData);

	public int deleteById(String id);

	public ProducerServerStatsData findById(String id);
	
	public ProducerServerStatsData findByTimeKey(long timeKey);
	
	public List<ProducerServerStatsData> findSectionData(long startKey, long endKey);
	
}
