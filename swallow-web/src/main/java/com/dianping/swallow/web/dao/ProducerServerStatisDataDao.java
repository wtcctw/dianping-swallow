package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.statis.ProducerServerStatisData;

public interface ProducerServerStatisDataDao {
	
	public boolean insert(ProducerServerStatisData statisData);

	public boolean update(ProducerServerStatisData statisData);

	public int deleteById(String id);

	public ProducerServerStatisData findById(String id);
	
	public ProducerServerStatisData findByTimeKey(String timeKey);
	
	public ProducerServerStatisData findByTopic(String topicName);

	public List<ProducerServerStatisData> findAll();

}
