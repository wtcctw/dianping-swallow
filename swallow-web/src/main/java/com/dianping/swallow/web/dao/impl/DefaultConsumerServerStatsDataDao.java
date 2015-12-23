package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:46
 */
@Service("consumerServerStatsDataDao")
public class DefaultConsumerServerStatsDataDao extends AbstractStatsDao implements ConsumerServerStatsDataDao {

	private static final String CONSUMERSERVERSTATSDATA_COLLECTION = "CONSUMER_SERVER_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ConsumerServerStatsData serverStatsData) {
		try {
			mongoTemplate.save(serverStatsData, CONSUMERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save consumer server statsdata." + serverStatsData, e);
		}
		return false;
	}

	@Override
	public boolean insert(List<ConsumerServerStatsData> serverStatsDatas) {
		try {
			mongoTemplate.insert(serverStatsDatas, CONSUMERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save consumer server statsdatas.", e);
		}
		return false;
	}

	public boolean removeLessThanTimeKey(long timeKey) {
		try {
			Query query = new Query(Criteria.where(TIMEKEY_FIELD).lt(timeKey));
			mongoTemplate.remove(query, CONSUMERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[removeLessThanTimeKey] remove less than timeKey error.", e);
		}
		return false;
	}

	@Override
	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).lte(endKey));
		List<ConsumerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}

	@Override
	public List<ConsumerServerStatsData> findSectionData(long startKey, long endKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(
				Direction.ASC, TIMEKEY_FIELD)));
		List<ConsumerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}

	@Override
	public ConsumerServerStatsData findOldestData() {
		Query query = new Query();
		query.skip(0).limit(1).with(new Sort(new Sort.Order(Direction.ASC, TIMEKEY_FIELD)));
		ConsumerServerStatsData statsData = mongoTemplate.findOne(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return statsData;
	}

}
