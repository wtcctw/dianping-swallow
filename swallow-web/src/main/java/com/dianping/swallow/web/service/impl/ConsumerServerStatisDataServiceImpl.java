package com.dianping.swallow.web.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerServerStatisData;
import com.dianping.swallow.web.service.ConsumerServerStatisDataService;

@Service("consumerServerStatisDataService")
public class ConsumerServerStatisDataServiceImpl implements ConsumerServerStatisDataService {

	@Autowired
	private ConsumerServerStatisDataDao consumerServerStatisDataDao;
	@Override
	public boolean insert(ConsumerServerStatisData statisData) {
		return consumerServerStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerServerStatisData statisData) {
		return consumerServerStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerServerStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerServerStatisData findById(String id) {
		return consumerServerStatisDataDao.findById(id);
	}

	@Override
	public ConsumerServerStatisData findByTimeKey(long timeKey) {
		return consumerServerStatisDataDao.findByTimeKey(timeKey);
	}

}
