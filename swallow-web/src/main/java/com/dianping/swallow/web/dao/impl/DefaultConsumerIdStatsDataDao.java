package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.mongodb.WriteResult;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:41
 */
@Service("consumerIdStatsDataDao")
public class DefaultConsumerIdStatsDataDao extends AbstractStatsDao implements ConsumerIdStatsDataDao {

	private static final String CONSUMERIDSTATSDATA_COLLECTION = "ConsumerIdStatsData";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerIdStatsData consumerIdstatsData) {
		try {
			mongoTemplate.save(consumerIdstatsData, CONSUMERIDSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save consumerid stats data " + consumerIdstatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerIdStatsData consumerIdstatsData) {
		return insert(consumerIdstatsData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerIdStatsData.class, CONSUMERIDSTATSDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerIdStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerIdStatsData consumerIdstatsData = mongoTemplate.findOne(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return consumerIdstatsData;
	}

	@Override
	public List<ConsumerIdStatsData> findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTime(String topicName, long timeKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findByTopicAndTimeAndConsumerId(String topicName, long timeKey, String consumerId) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).is(timeKey)
				.and(CONSUMERID_FIELD).is(consumerId));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId)
				.and(TIMEKEY_FIELD).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(Direction.ASC,
				TIMEKEY_FIELD)));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

}
