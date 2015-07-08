package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerTopicStatisData;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;


@Service("producerTopicStatisDataService")
public class ProducerTopicStatisDataServiceImpl implements ProducerTopicStatisDataService {

	@Autowired
	private ProducerTopicStatisDataDao producerTopicStatisDataDao;
	
	@Override
	public boolean insert(ProducerTopicStatisData statisData) {
		return producerTopicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ProducerTopicStatisData statisData) {
		return producerTopicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return producerTopicStatisDataDao.deleteById(id);
	}

	@Override
	public ProducerTopicStatisData findById(String id) {
		return producerTopicStatisDataDao.findById(id);
	}

	@Override
	public ProducerTopicStatisData findByTimeKey(long timeKey) {
		return producerTopicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ProducerTopicStatisData> findByTopic(String topicName) {
		return producerTopicStatisDataDao.findByTopic(topicName);
	}

}
