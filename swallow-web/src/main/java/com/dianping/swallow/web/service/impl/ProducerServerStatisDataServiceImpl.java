package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.statis.ProducerServerStatisData;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;

@Service("producerServerStatisDataService")
public class ProducerServerStatisDataServiceImpl implements ProducerServerStatisDataService {

	@Override
	public boolean insert(ProducerServerStatisData statisData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ProducerServerStatisData statisData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProducerServerStatisData findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProducerServerStatisData findByTimeKey(String timeKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProducerServerStatisData findByTopic(String topicName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProducerServerStatisData> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
