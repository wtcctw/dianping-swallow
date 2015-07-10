package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerTopicStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerTopicStatisDataService;

/**
*
* @author qiyin
*
*/
@Service("consumerTopicStatisDataService")
public class ConsumerTopicStatisDataServiceImpl implements ConsumerTopicStatisDataService {

	@Autowired
	private ConsumerTopicStatisDataDao consumerTopicStatisDataDao;
	
	@Override
	public boolean insert(ConsumerTopicStatsData statisData) {
		return consumerTopicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerTopicStatsData statisData) {
		return consumerTopicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerTopicStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerTopicStatsData findById(String id) {
		return consumerTopicStatisDataDao.findById(id);
	}

	@Override
	public ConsumerTopicStatsData findByTimeKey(long timeKey) {
		return consumerTopicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ConsumerTopicStatsData> findByTopic(String topicName) {
		return consumerTopicStatisDataDao.findByTopic(topicName);
	}

}
