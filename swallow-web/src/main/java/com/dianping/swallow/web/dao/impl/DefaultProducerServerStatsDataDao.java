package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:54
 */
@Service("producerServerStatsDataDao")
public class DefaultProducerServerStatsDataDao extends AbstractStatsDao implements ProducerServerStatsDataDao {

	private static final String PRODUCERSERVERSTATSDATA_COLLECTION = "PRODUCER_SERVER_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ProducerServerStatsData serverStatsData) {
		try {
			mongoTemplate.save(serverStatsData, PRODUCERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save producer server statsdata." + serverStatsData, e);
		}
		return false;
	}

	@Override
	public boolean insert(List<ProducerServerStatsData> serverStatsDatas) {
		try {
			mongoTemplate.save(serverStatsDatas, PRODUCERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save producer server statsdatas.", e);
		}
		return false;
	}

	@Override
	public List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).lte(endKey));
		List<ProducerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}

	@Override
	public List<ProducerServerStatsData> findSectionData(long startKey, long endKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(
				Direction.ASC, TIMEKEY_FIELD)));
		List<ProducerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}
}
