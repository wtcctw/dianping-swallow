package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerTopicStatisDataDao;
import com.dianping.swallow.web.dao.TopicStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatisData;
import com.dianping.swallow.web.model.statis.TopicStatisData;
import com.dianping.swallow.web.service.ConsumerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicStatisDataService;


@Service("consumerTopicStatisDataService")
public class ConsumerTopicStatisDataServiceImpl implements ConsumerTopicStatisDataService {

	@Autowired
	private ConsumerTopicStatisDataDao consumerTopicStatisDataDao;
	
	@Override
	public boolean insert(ConsumerTopicStatisData statisData) {
		return consumerTopicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerTopicStatisData statisData) {
		return consumerTopicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerTopicStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerTopicStatisData findById(String id) {
		return consumerTopicStatisDataDao.findById(id);
	}

	@Override
	public ConsumerTopicStatisData findByTimeKey(long timeKey) {
		return consumerTopicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ConsumerTopicStatisData> findByTopic(String topicName) {
		return consumerTopicStatisDataDao.findByTopic(topicName);
	}

}
