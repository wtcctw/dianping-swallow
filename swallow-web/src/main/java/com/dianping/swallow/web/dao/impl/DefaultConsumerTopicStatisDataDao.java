package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerTopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatisData;
import com.mongodb.WriteResult;

@Service("consumerTopicStatisDataDao")
public class DefaultConsumerTopicStatisDataDao extends AbstractWriteDao implements ConsumerTopicStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebconsumertopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerTopicStatisData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerTopicStatisData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerTopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerTopicStatisData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerTopicStatisData topicStatisData = mongoTemplate.findOne(query, ConsumerTopicStatisData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public ConsumerTopicStatisData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ConsumerTopicStatisData topicStatisData = mongoTemplate.findOne(query, ConsumerTopicStatisData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<ConsumerTopicStatisData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ConsumerTopicStatisData> topicStatisDatas = mongoTemplate.find(query, ConsumerTopicStatisData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

}
