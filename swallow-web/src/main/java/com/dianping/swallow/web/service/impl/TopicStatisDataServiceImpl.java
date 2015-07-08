package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicStatisDataDao;
import com.dianping.swallow.web.model.statis.TopicStatisData;
import com.dianping.swallow.web.service.TopicStatisDataService;


@Service("topicStatisDataService")
public class TopicStatisDataServiceImpl implements TopicStatisDataService {

	@Autowired
	private TopicStatisDataDao topicStatisDataDao;
	
	@Override
	public boolean insert(TopicStatisData statisData) {
		return topicStatisDataDao.insert(statisData);
	}

	@Override
	public boolean update(TopicStatisData statisData) {
		return topicStatisDataDao.update(statisData);
	}

	@Override
	public int deleteById(String id) {
		return topicStatisDataDao.deleteById(id);
	}

	@Override
	public TopicStatisData findById(String id) {
		return topicStatisDataDao.findById(id);
	}

	@Override
	public TopicStatisData findByTimeKey(long timeKey) {
		return topicStatisDataDao.findByTimeKey(timeKey);
	}

	@Override
	public List<TopicStatisData> findByTopic(String topicName) {
		return topicStatisDataDao.findByTopic(topicName);
	}

}
