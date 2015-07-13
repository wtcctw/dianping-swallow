package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;

/**
 *
 * @author qiyin
 *
 */
@Service("producerTopicStatisDataService")
public class ProducerTopicStatisDataServiceImpl implements ProducerTopicStatisDataService {

	@Autowired
	private ProducerTopicStatisDataDao producerTopicStatisDataDao;

	@Override
	public boolean insert(ProducerTopicStatsData statisData) {
		return producerTopicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(ProducerTopicStatsData statisData) {
		return producerTopicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return producerTopicStatisDataDao.deleteById(id);
	}

	@Override
	public ProducerTopicStatsData findById(String id) {
		return producerTopicStatisDataDao.findById(id);
	}

	@Override
	public ProducerTopicStatsData findByTimeKey(long timeKey) {
		return producerTopicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName) {
		return producerTopicStatisDataDao.findByTopic(topicName);
	}

	@Override
	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		return producerTopicStatisDataDao.findSectionData(topicName, startKey, endKey);
	}

}
