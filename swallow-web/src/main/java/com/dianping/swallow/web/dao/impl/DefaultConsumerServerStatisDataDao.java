package com.dianping.swallow.web.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.mongodb.WriteResult;

/**
*
* @author qiyin
*
*/
@Service("consumerServerStatisDataDao")
public class DefaultConsumerServerStatisDataDao extends AbstractWriteDao implements ConsumerServerStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerServerStatisDataDao.class);

	private static final String CONSUMERSERVERSTATICDATA_COLLECTION = "swallowwebconsumerserverstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ConsumerServerStatsData statisData) {
		try {
			mongoTemplate.save(statisData, CONSUMERSERVERSTATICDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerServerStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATICDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerServerStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerServerStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ConsumerServerStatsData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ConsumerServerStatsData serverStatisData = mongoTemplate.findOne(query, ConsumerServerStatsData.class,
				CONSUMERSERVERSTATICDATA_COLLECTION);
		return serverStatisData;
	}

}
