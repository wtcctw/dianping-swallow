package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.mongodb.WriteResult;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:38:46
 */
@Service("consumerServerStatsDataDao")
public class DefaultConsumerServerStatsDataDao extends AbstractStatsDao implements ConsumerServerStatsDataDao {

	private static final String CONSUMERSERVERSTATSDATA_COLLECTION = "ConsumerServerStatsData";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ConsumerServerStatsData serverStatsData) {
		try {
			mongoTemplate.save(serverStatsData, CONSUMERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save consumer machine statis dao " + serverStatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerServerStatsData serverStatsData) {
		return insert(serverStatsData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerServerStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerServerStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ConsumerServerStatsData findByTimeKey(String ip, long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey).and(IP_FIELD).is(ip));
		ConsumerServerStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).lte(endKey));
		List<ConsumerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}

}
