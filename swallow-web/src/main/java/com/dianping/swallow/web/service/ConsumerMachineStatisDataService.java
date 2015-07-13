package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;

/**
 * 
 * @author qiyin
 *
 */
public interface ConsumerMachineStatisDataService {

	public boolean insert(ConsumerMachineStatsData statisData);

	public boolean update(ConsumerMachineStatsData statisData);

	public int deleteById(String id);

	public ConsumerMachineStatsData findById(String id);

	public ConsumerMachineStatsData findByTimeKey(String ip, long timeKey);

	public List<ConsumerMachineStatsData> findSectionData(String ip, long startKey, long endKey);
	
}
