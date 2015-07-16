package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerMachineStatisDataDao;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.mongodb.WriteResult;

/**
 * 
 * @author qiyin
 *
 */
@Service("producerMachineStatisDataDao")
public class DefaultProducerMachineStatisDataDao extends AbstractWriteDao implements ProducerMachineStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServerStatisDataDao.class);

	private static final String PRODUCERMACHINESTATICDATA_COLLECTION = "swallowwebproducermachinestatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ProducerMachineStatsData statisData) {
		try {
			mongoTemplate.save(statisData, PRODUCERMACHINESTATICDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save producer machine statis data " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerMachineStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerMachineStatsData.class,
				PRODUCERMACHINESTATICDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerMachineStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerMachineStatsData serverStatisData = mongoTemplate.findOne(query, ProducerMachineStatsData.class,
				PRODUCERMACHINESTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ProducerMachineStatsData findByTimeKey(String ip, long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey).and(IP_FIELD).is(ip));
		ProducerMachineStatsData serverStatisData = mongoTemplate.findOne(query, ProducerMachineStatsData.class,
				PRODUCERMACHINESTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public List<ProducerMachineStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).and(TIMEKEY_FIELD)
				.lte(endKey));
		List<ProducerMachineStatsData> serverStatisDatas = mongoTemplate.find(query, ProducerMachineStatsData.class,
				PRODUCERMACHINESTATICDATA_COLLECTION);
		return serverStatisDatas;
	}

}
