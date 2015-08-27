package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdReStatsDataDao;
import com.dianping.swallow.web.monitor.model.ConsumerIdReStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午12:40:24
 */
@Service("consumerIdReStatsDataDao")
public class DefaultConsumeridReStatsDataDao extends AbstractStatsDao implements ConsumerIdReStatsDataDao {

	private static final String CONSUMERIDRESTATSDATA_COLLECTION = "CONSUMERID_RE_STATS_DATA";

	private static final String FROMTIMEKEY_FIELD = "fromTimeKey";

	private static final String TOTIMEKEY_FIELD = "toTimeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

	@Override
	public boolean insert(ConsumerIdReStatsData consumerIdReStatsData) {
		try {
			mongoTemplate.save(consumerIdReStatsData, CONSUMERIDRESTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save consumerid re stats data " + consumerIdReStatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerIdReStatsData consumerIdReStatsData) {
		return insert(consumerIdReStatsData);
	}

	@Override
	public List<ConsumerIdReStatsData> findByPage(String topicName, String consumerId, int limit, int offset) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId))
				.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.DESC, FROMTIMEKEY_FIELD)));
		List<ConsumerIdReStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdReStatsData.class,
				CONSUMERIDRESTATSDATA_COLLECTION);
		return statisDatas;
	}

	@Override
	public List<ConsumerIdReStatsData> findByTimeKey(long fromTimeKey, long toTimeKey) {
		Query query = new Query(Criteria.where(FROMTIMEKEY_FIELD).gte(fromTimeKey).and(TOTIMEKEY_FIELD).lte(toTimeKey))
				.with(new Sort(new Sort.Order(Direction.ASC, FROMTIMEKEY_FIELD)));
		List<ConsumerIdReStatsData> statisDatas = mongoTemplate.find(query, ConsumerIdReStatsData.class,
				CONSUMERIDRESTATSDATA_COLLECTION);
		return statisDatas;
	}

}
