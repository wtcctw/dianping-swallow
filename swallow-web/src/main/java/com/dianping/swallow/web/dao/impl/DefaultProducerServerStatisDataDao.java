package com.dianping.swallow.web.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ProducerServerStatisData;
import com.mongodb.WriteResult;

@Service("producerServerStatisDataDao")
public class DefaultProducerServerStatisDataDao extends AbstractWriteDao implements ProducerServerStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServerStatisDataDao.class);

	private static final String PRODUCERSERVERSTATICDATA_COLLECTION = "swallowwebproducerserverstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ProducerServerStatisData statisData) {
		try {
			mongoTemplate.save(statisData, PRODUCERSERVERSTATICDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerServerStatisData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerServerStatisData.class,
				PRODUCERSERVERSTATICDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerServerStatisData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerServerStatisData serverStatisData = mongoTemplate.findOne(query, ProducerServerStatisData.class,
				PRODUCERSERVERSTATICDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ProducerServerStatisData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ProducerServerStatisData serverStatisData = mongoTemplate.findOne(query, ProducerServerStatisData.class,
				PRODUCERSERVERSTATICDATA_COLLECTION);
		return serverStatisData;
	}

}
