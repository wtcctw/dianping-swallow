package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;

/**
 * 
 * @author qiyin
 *
 */
public interface ProducerMachineStatisDataService {

	public boolean insert(ProducerMachineStatsData statisData);

	public boolean update(ProducerMachineStatsData statisData);

	public int deleteById(String id);

	public ProducerMachineStatsData findById(String id);

	public ProducerMachineStatsData findByTimeKey(String ip, long timeKey);

	public List<ProducerMachineStatsData> findSectionData(String ip, long startKey, long endKey);

}
