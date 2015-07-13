package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerMachineStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;
import com.dianping.swallow.web.service.ConsumerMachineStatisDataService;

/**
 * 
 * @author qiyin
 *
 */
@Service("consumerMachineStatisDataService")
public class ConsumerMachineStatisDataServiceImpl implements ConsumerMachineStatisDataService {

	@Autowired
	private ConsumerMachineStatisDataDao consumerMachineStatisDataDao;

	@Override
	public boolean insert(ConsumerMachineStatsData statisData) {
		return consumerMachineStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerMachineStatsData statisData) {
		return consumerMachineStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerMachineStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerMachineStatsData findById(String id) {
		return consumerMachineStatisDataDao.findById(id);
	}

	@Override
	public ConsumerMachineStatsData findByTimeKey(String ip, long timeKey) {
		return consumerMachineStatisDataDao.findByTimeKey(ip, timeKey);
	}

	@Override
	public List<ConsumerMachineStatsData> findSectionData(String ip, long startKey, long endKey) {
		return consumerMachineStatisDataDao.findSectionData(ip, startKey, endKey);
	}

}
