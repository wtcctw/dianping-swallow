package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ProducerTopicStatisData;
import com.mongodb.WriteResult;

@Service("producerTopicStatisDataDao")
public class DefaultProducerTopicStatisDataDao extends AbstractWriteDao implements ProducerTopicStatisDataDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebproducertopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";
	
	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ProducerTopicStatisData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerTopicStatisData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerTopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerTopicStatisData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerTopicStatisData topicStatisData = mongoTemplate.findOne(query, ProducerTopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public ProducerTopicStatisData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ProducerTopicStatisData topicStatisData = mongoTemplate.findOne(query, ProducerTopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<ProducerTopicStatisData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ProducerTopicStatisData> topicStatisDatas = mongoTemplate.find(query, ProducerTopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

}
