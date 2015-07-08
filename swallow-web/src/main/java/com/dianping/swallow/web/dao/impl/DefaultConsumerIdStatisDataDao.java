package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ConsumerIdStatisData;
import com.mongodb.WriteResult;

@Service("consumerIdStatisDataDao")
public class DefaultConsumerIdStatisDataDao extends AbstractWriteDao implements ConsumerIdStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerIdStatisDataDao.class);

	private static final String CONSUMERIDTATISDATA_COLLECTION = "swallowwebconsumeridstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerIdStatisData statisData) {
		try {
			mongoTemplate.save(statisData, CONSUMERIDTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerIdStatisData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerIdStatisData.class, CONSUMERIDTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerIdStatisData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerIdStatisData statisData = mongoTemplate.findOne(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisData;
	}

	@Override
	public List<ConsumerIdStatisData> findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatisData> statisDatas = mongoTemplate.find(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatisData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ConsumerIdStatisData> statisDatas = mongoTemplate.find(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndTime(String topicName, long timeKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatisData> statisDatas = mongoTemplate.find(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndConsumerId(String topicName, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatisData> statisDatas = mongoTemplate.find(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatisData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey)
				.and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatisData> statisDatas = mongoTemplate.find(query, ConsumerIdStatisData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

}
