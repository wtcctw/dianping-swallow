package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIpStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:27:07
 */
@Service("consumerIpStatsDataDao")
public class DefaultConsumerIpStatsDataDao extends AbstractStatsDao implements ConsumerIpStatsDataDao {

	private static final String CONSUMERIPSTATSDATA_COLLECTION = "CONSUMER_IP_STATS_DATA";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String CONSUMERID_FIELD = "consumerId";

	@Override
	public boolean insert(ConsumerIpStatsData ipStatsData) {
		try {
			mongoTemplate.save(ipStatsData, CONSUMERIPSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save consumer ip statsdata." + ipStatsData, e);
		}
		return false;
	}

	@Override
	public boolean insert(List<ConsumerIpStatsData> ipStatsDatas) {
		try {
			mongoTemplate.insert(ipStatsDatas, CONSUMERIPSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save consumer ip statsdatas.", e);
		}
		return false;
	}

	@Override
	public List<ConsumerIpStatsData> find(String topicName, String consumerId, String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(CONSUMERID_FIELD).is(consumerId)
				.and(TIMEKEY_FIELD).gte(startKey).lte(endKey)).with(new Sort(new Sort.Order(Direction.ASC,
				TIMEKEY_FIELD)));
		List<ConsumerIpStatsData> statisDatas = mongoTemplate.find(query, ConsumerIpStatsData.class,
				CONSUMERIPSTATSDATA_COLLECTION);
		return statisDatas;
	}

}
