package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerTopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;
import com.mongodb.WriteResult;

/**
*
* @author qiyin
*
*/
@Service("consumerTopicStatisDataDao")
public class DefaultConsumerTopicStatisDataDao extends AbstractWriteDao implements ConsumerTopicStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebconsumertopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerTopicStatsData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerTopicStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerTopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerTopicStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerTopicStatsData topicStatisData = mongoTemplate.findOne(query, ConsumerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public ConsumerTopicStatsData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ConsumerTopicStatsData topicStatisData = mongoTemplate.findOne(query, ConsumerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<ConsumerTopicStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ConsumerTopicStatsData> topicStatisDatas = mongoTemplate.find(query, ConsumerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

}
