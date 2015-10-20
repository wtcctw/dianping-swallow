package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerIpStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:39:10
 */
@Service("producerIpStatsDataDao")
public class DefaultProducerIpStatsDataDao extends AbstractStatsDao implements ProducerIpStatsDataDao {

	private static final String PRODUCERIPSTATSDATA_COLLECTION = "PRODUCER_IP_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	@Override
	public boolean insert(ProducerIpStatsData ipStatsData) {
		try {
			mongoTemplate.save(ipStatsData, PRODUCERIPSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save producer ip statsdata." + ipStatsData, e);
		}
		return false;
	}

	@Override
	public boolean insert(List<ProducerIpStatsData> ipStatsDatas) {
		try {
			mongoTemplate.save(ipStatsDatas, PRODUCERIPSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save producer ip statsdatas.", e);
		}
		return false;
	}

	@Override
	public List<ProducerIpStatsData> find(String topicName, String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).gte(startKey)
				.lte(endKey)).with(new Sort(new Sort.Order(Direction.ASC, TIMEKEY_FIELD)));
		List<ProducerIpStatsData> statisDatas = mongoTemplate.find(query, ProducerIpStatsData.class,
				PRODUCERIPSTATSDATA_COLLECTION);
		return statisDatas;
	}

}
