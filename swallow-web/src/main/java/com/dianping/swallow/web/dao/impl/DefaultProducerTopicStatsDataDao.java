package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:59
 */
@Service("producerTopicStatsDataDao")
public class DefaultProducerTopicStatsDataDao extends AbstractStatsDao implements ProducerTopicStatsDataDao {

	private static final String TOPICSTATSDATA_COLLECTION = "PRODUCER_TOPIC_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	@Override
	public boolean insert(ProducerTopicStatsData topicStatsData) {
		try {
			mongoTemplate.save(topicStatsData, TOPICSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save producer topic statsdata." + topicStatsData, e);
		}
		return false;
	}

	@Override
	public boolean insert(List<ProducerTopicStatsData> topicStatsDatas) {
		try {
			mongoTemplate.insert(topicStatsDatas, TOPICSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save producer topic statsdatas.", e);
		}
		return false;
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName, int offset, int limit) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, TIMEKEY_FIELD)));
		List<ProducerTopicStatsData> topicStatsDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return topicStatsDatas;
	}

	@Override
	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).gte(startKey)
				.lte(endKey));
		List<ProducerTopicStatsData> topicStatsDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return topicStatsDatas;
	}

	@Override
	public ProducerTopicStatsData findOneByTopicAndTime(String topicName, long startKey, long endKey, boolean isGt) {
		Criteria criteria = Criteria.where(TOPICNAME_FIELD).is(topicName);
		criteria.and(TIMEKEY_FIELD).gte(startKey).lte(endKey);
		Query query = new Query(criteria);
		query.skip(0).limit(1).with(new Sort(new Sort.Order(isGt ? Direction.ASC : Direction.DESC, TIMEKEY_FIELD)));
		ProducerTopicStatsData statsData = mongoTemplate.findOne(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return statsData;
	}

}
