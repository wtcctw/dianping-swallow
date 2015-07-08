package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;

@Service("consumerIdStatisDataService")
public class ConsumerIdStatisDataServiceImpl implements ConsumerIdStatisDataService {
	
	@Autowired
	private ConsumerIdStatisDataDao consumerIdStatisDataDao;

	@Override
	public boolean insert(ConsumerIdStatisData statisData) {
		return consumerIdStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerIdStatisData statisData) {
		return consumerIdStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerIdStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerIdStatisData findById(String id) {
		return consumerIdStatisDataDao.findById(id);
	}

	@Override
	public List<ConsumerIdStatisData> findByTimeKey(long timeKey) {
		return consumerIdStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ConsumerIdStatisData> findByTopic(String topicName) {
		return consumerIdStatisDataDao.findByTopic(topicName);
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndTime(String topicName, long timeKey) {
		return consumerIdStatisDataDao.findByTopicAndTime(topicName, timeKey);
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndConsumerId(String topicName, String consumerId) {
		return consumerIdStatisDataDao.findByTopicAndConsumerId(topicName, consumerId);
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		return consumerIdStatisDataDao.findByTopicAndTimeAndConsumerId(topicName, timeKey, consumerId);
	}

}
