package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.statis.TopicStatisData;
import com.dianping.swallow.web.service.TopicStatisDataService;

@Service("topicStatisDataService")
public class TopicStatisDataServiceImpl implements TopicStatisDataService {

	@Override
	public boolean insert(TopicStatisData statisData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(TopicStatisData statisData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TopicStatisData findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicStatisData findByTimeKey(String timeKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicStatisData findByTopic(String topicName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopicStatisData> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
