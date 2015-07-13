package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerIdStatisDataService")
public class ConsumerIdStatisDataServiceImpl implements ConsumerIdStatisDataService {

	@Autowired
	private ConsumerIdStatisDataDao consumerIdStatisDataDao;

	@Override
	public boolean insert(ConsumerIdStatsData statisData) {
		return consumerIdStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ConsumerIdStatsData statisData) {
		return consumerIdStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return consumerIdStatisDataDao.deleteById(id);
	}

	@Override
	public ConsumerIdStatsData findById(String id) {
		return consumerIdStatisDataDao.findById(id);
	}

	@Override
	public List<ConsumerIdStatsData> findByTimeKey(long timeKey) {
		return consumerIdStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopic(String topicName) {
		return consumerIdStatisDataDao.findByTopic(topicName);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey) {
		return consumerIdStatisDataDao.findByTopicAndTime(topicName, timeKey);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId) {
		return consumerIdStatisDataDao.findByTopicAndConsumerId(topicName, consumerId);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		return consumerIdStatisDataDao.findByTopicAndTimeAndConsumerId(topicName, timeKey, consumerId);
	}

	@Override
	public List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey) {
		return consumerIdStatisDataDao.findSectionData(topicName, consumerId, startKey, endKey);
	}

}
