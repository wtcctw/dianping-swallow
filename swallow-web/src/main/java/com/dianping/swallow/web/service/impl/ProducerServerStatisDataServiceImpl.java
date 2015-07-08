package com.dianping.swallow.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerServerStatisData;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;


@Service("producerServerStatisDataService")
public class ProducerServerStatisDataServiceImpl implements ProducerServerStatisDataService {

	@Autowired
	private ProducerServerStatisDataDao producerServerStatisDataDao;
	
	@Override
	public boolean insert(ProducerServerStatisData statisData) {
		return producerServerStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ProducerServerStatisData statisData) {
		return producerServerStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return producerServerStatisDataDao.deleteById(id);
	}

	@Override
	public ProducerServerStatisData findById(String id) {
		return producerServerStatisDataDao.findById(id);
	}

	@Override
	public ProducerServerStatisData findByTimeKey(long timeKey) {
		return producerServerStatisDataDao.findByTimeKey(timeKey);
	}

}
