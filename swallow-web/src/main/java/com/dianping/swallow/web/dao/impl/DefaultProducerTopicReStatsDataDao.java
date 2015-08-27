package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicReStatsDataDao;
import com.dianping.swallow.web.monitor.model.ProducerTopicReStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午12:47:01
 */
@Service("producerTopicReStatsDataDao")
public class DefaultProducerTopicReStatsDataDao extends AbstractStatsDao implements ProducerTopicReStatsDataDao {

	private static final String TOPICRESTATSDATA_COLLECTION = "PRODUCER_TOPIC_RE_STATS_DATA";

	private static final String FROMTIMEKEY_FIELD = "fromTimeKey";

	private static final String TOTIMEKEY_FIELD = "toTimeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	@Override
	public boolean insert(ProducerTopicReStatsData topicReStatsData) {
		try {
			mongoTemplate.save(topicReStatsData, TOPICRESTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save topic re stats data " + topicReStatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerTopicReStatsData topicReStatsData) {
		return insert(topicReStatsData);
	}

	@Override
	public List<ProducerTopicReStatsData> findByPage(String topicName, int limit, int offset) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName)).skip(offset).limit(limit)
				.with(new Sort(new Sort.Order(Direction.DESC, FROMTIMEKEY_FIELD)));
		List<ProducerTopicReStatsData> statisDatas = mongoTemplate.find(query, ProducerTopicReStatsData.class,
				TOPICRESTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ProducerTopicReStatsData> findByTimeKey(long fromTimeKey, long toTimeKey) {
		Query query = new Query(Criteria.where(FROMTIMEKEY_FIELD).gte(fromTimeKey).and(TOTIMEKEY_FIELD).lte(toTimeKey))
				.with(new Sort(new Sort.Order(Direction.ASC, FROMTIMEKEY_FIELD)));
		List<ProducerTopicReStatsData> statisDatas = mongoTemplate.find(query, ProducerTopicReStatsData.class,
				TOPICRESTATSDATA_COLLECTION);
		return statisDatas;
	}

}
