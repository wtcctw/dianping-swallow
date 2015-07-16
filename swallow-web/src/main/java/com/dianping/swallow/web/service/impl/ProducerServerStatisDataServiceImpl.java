package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;

/**
*
* @author qiyin
*
*/
@Service("producerServerStatisDataService")
public class ProducerServerStatisDataServiceImpl implements ProducerServerStatisDataService {

	@Autowired
	private ProducerServerStatisDataDao producerServerStatisDataDao;

	@Override
	public boolean insert(ProducerServerStatsData statisData) {
		return producerServerStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ProducerServerStatsData statisData) {
		return producerServerStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return producerServerStatisDataDao.deleteById(id);
	}

	@Override
	public ProducerServerStatsData findById(String id) {
		return producerServerStatisDataDao.findById(id);
	}

	@Override
	public ProducerServerStatsData findByTimeKey(long timeKey) {
		return producerServerStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ProducerServerStatsData> findSectionData(long startKey, long endKey) {
		return producerServerStatisDataDao.findSectionData(startKey, endKey);
	}

}
