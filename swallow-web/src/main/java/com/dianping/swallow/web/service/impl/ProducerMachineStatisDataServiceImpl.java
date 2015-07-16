package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerMachineStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.service.ProducerMachineStatisDataService;

/**
 * 
 * @author qiyin
 *
 */
@Service("producerMachineStatisDataService")
public class ProducerMachineStatisDataServiceImpl implements ProducerMachineStatisDataService {

	@Autowired
	private ProducerMachineStatisDataDao producerMachineStatisDataDao;

	@Override
	public boolean insert(ProducerMachineStatsData statisData) {
		return producerMachineStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ProducerMachineStatsData statisData) {
		return producerMachineStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return producerMachineStatisDataDao.deleteById(id);
	}

	@Override
	public ProducerMachineStatsData findById(String id) {
		return producerMachineStatisDataDao.findById(id);
	}

	@Override
	public ProducerMachineStatsData findByTimeKey(String ip, long timeKey) {
		return producerMachineStatisDataDao.findByTimeKey(ip, timeKey);
	}

	@Override
	public List<ProducerMachineStatsData> findSectionData(String ip, long startKey, long endKey) {
		return producerMachineStatisDataDao.findSectionData(ip, startKey, endKey);
	}

}
