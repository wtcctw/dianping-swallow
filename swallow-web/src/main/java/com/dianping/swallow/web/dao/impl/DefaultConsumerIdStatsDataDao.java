package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:41
 */
@Service("consumerIdStatsDataDao")
public class DefaultConsumerIdStatsDataDao extends AbstractStatsDao implements ConsumerIdStatsDataDao {

	private static final String CONSUMERIDSTATSDATA_COLLECTION = "CONSUMERID_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

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
	public List<ConsumerIdStatsData> findByTopicAndConsumerId(String topicName, String consumerId, int offset, int limit) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId));
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, TIMEKEY_FIELD)));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public ConsumerIdStatsData findOneByTopicAndTimeAndConsumerId(String topicName, String consumerId, long startKey,
			long endKey, boolean isGt) {
		Criteria criteria = Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId);
		criteria.and(TIMEKEY_FIELD).gte(startKey).lte(endKey);
		Query query = new Query(criteria);
		query.skip(0).limit(1).with(new Sort(new Sort.Order(isGt ? Direction.ASC : Direction.DESC, TIMEKEY_FIELD)));
		ConsumerIdStatsData statsData = mongoTemplate.findOne(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statsData;
	}

	@Override
	public List<ConsumerIdStatsData> findSectionData(String topicName, String consumerId, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId)
				.and(TIMEKEY_FIELD).gte(startKey).lte(endKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdStatsData> findSectionData(String topicName, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).gte(startKey)
				.lte(endKey));
		List<ConsumerIdStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdStatsData.class,
				CONSUMERIDSTATSDATA_COLLECTION);
		return statisDatas;
	}

}
