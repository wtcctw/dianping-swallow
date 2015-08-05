package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午3:17:35
 */
@Service("consumerIdStatsDataService")
public class ConsumerIdStatsDataServiceImpl implements ConsumerIdStatsDataService {
	
	@Autowired
	private ConsumerIdStatsDataDao consumerIdStatsDataDao;
	
	@Override
	public boolean insert(ConsumerIdStatsData consumerIdstatsData) {
		return consumerIdStatsDataDao.insert(consumerIdstatsData);
	}

	@Override
	public boolean update(ConsumerIdStatsData consumerIdstatsData) {
		return consumerIdStatsDataDao.update(consumerIdstatsData);
	}

	@Override
	public int deleteById(String id) {
		return consumerIdStatsDataDao.deleteById(id);
	}

	@Override
	public ConsumerIdStatsData findById(String id) {
		return consumerIdStatsDataDao.findById(id);
	}

	@Override
	public List<ConsumerIdStatsData> findByTimeKey(long timeKey) {
		return consumerIdStatsDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopic(String topicName) {
		return consumerIdStatsDataDao.findByTopic(topicName);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey) {
		return consumerIdStatsDataDao.findByTopicAndTime(topicName, timeKey);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId) {
		return consumerIdStatsDataDao.findByTopicAndConsumerId(topicName, consumerId);
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		return consumerIdStatsDataDao.findByTopicAndTimeAndConsumerId(topicName, timeKey, consumerId);
	}

	@Override
	public List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey) {
		return consumerIdStatsDataDao.findSectionData(topicName, consumerId, startKey, endKey);
	}

}
