package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicStatisDataDao;
import com.dianping.swallow.web.model.statis.TopicStatsData;
import com.dianping.swallow.web.service.TopicStatisDataService;


@Service("topicStatisDataService")
public class TopicStatisDataServiceImpl implements TopicStatisDataService {

	@Autowired
	private TopicStatisDataDao topicStatisDataDao;
	
	@Override
	public boolean insert(TopicStatsData statisData) {
		return topicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(TopicStatsData statisData) {
		return topicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return topicStatisDataDao.deleteById(id);
	}

	@Override
	public TopicStatsData findById(String id) {
		return topicStatisDataDao.findById(id);
	}

	@Override
	public TopicStatsData findByTimeKey(long timeKey) {
		return topicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<TopicStatsData> findByTopic(String topicName) {
		return topicStatisDataDao.findByTopic(topicName);
	}

}
