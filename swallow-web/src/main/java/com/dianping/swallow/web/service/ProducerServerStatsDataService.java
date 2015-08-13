package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.stats.ProducerServerStatsData;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:39:51
 */
public interface ProducerServerStatsDataService {
	
	public boolean insert(ProducerServerStatsData serverStatsData);

	public boolean update(ProducerServerStatsData serverStatsData);

	public int deleteById(String id);

	public ProducerServerStatsData findById(String id);

	public ProducerServerStatsData findByTimeKey(String ip, long timeKey);

	public List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey);
	
}
