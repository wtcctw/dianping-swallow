package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.mongodb.WriteResult;

/**
*
* @author qiyin
*
*/
@Service("consumerIdStatisDataDao")
public class DefaultConsumerIdStatisDataDao extends AbstractWriteDao implements ConsumerIdStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerIdStatisDataDao.class);

	private static final String CONSUMERIDTATISDATA_COLLECTION = "swallowwebconsumeridstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerIdStatsData statisData) {
		try {
			mongoTemplate.save(statisData, CONSUMERIDTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerIdStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerIdStatsData.class, CONSUMERIDTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerIdStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerIdStatsData statisData = mongoTemplate.findOne(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisData;
	}

	@Override
	public List<ConsumerIdStatsData> findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey)
				.and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDTATISDATA_COLLECTION);
		return statisDatas;
	}

}
