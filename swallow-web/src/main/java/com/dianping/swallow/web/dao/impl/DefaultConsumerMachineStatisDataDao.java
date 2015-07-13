package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerMachineStatisDataDao;
import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;
import com.mongodb.WriteResult;

/**
 * 
 * @author qiyin
 *
 */
@Service("consumerMachineStatisDataDao")
public class DefaultConsumerMachineStatisDataDao extends AbstractWriteDao implements ConsumerMachineStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServerStatisDataDao.class);

	private static final String CONSUMERMACHINESTATICDATA_COLLECTION = "swallowwebconsumermachinestatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ConsumerMachineStatsData statisData) {
		try {
			mongoTemplate.save(statisData, CONSUMERMACHINESTATICDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerMachineStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerMachineStatsData.class,
				CONSUMERMACHINESTATICDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerMachineStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerMachineStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerMachineStatsData.class,
				CONSUMERMACHINESTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ConsumerMachineStatsData findByTimeKey(String ip, long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey).and(IP_FIELD).is(ip));
		ConsumerMachineStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerMachineStatsData.class,
				CONSUMERMACHINESTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public List<ConsumerMachineStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).and(TIMEKEY_FIELD)
				.lte(endKey));
		List<ConsumerMachineStatsData> serverStatisDatas = mongoTemplate.find(query, ConsumerMachineStatsData.class,
				CONSUMERMACHINESTATICDATA_COLLECTION);
		return serverStatisDatas;
	}

}
