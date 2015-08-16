package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.AlarmMetaDao;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.mongodb.WriteResult;

@Service("alarmMetaDao")
public class DefaultAlarmMetaDao extends AbstractWriteDao implements AlarmMetaDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultAlarmMetaDao.class);

	private static final String ALARMMETA_COLLECTION = "swallowwebalarmmetatestc";

	private static final String ID_FIELD = "id";

	private static final String METAID_FIELD = "metaId";

	@Override
	public boolean insert(AlarmMeta alarmMeta) {
		try {
			mongoTemplate.save(alarmMeta,ALARMMETA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save swallow alarm setting " + alarmMeta, e);
		}
		return false;
	}

	@Override
	public boolean update(AlarmMeta alarmMeta) {
		return insert(alarmMeta);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, AlarmMeta.class, ALARMMETA_COLLECTION);
		return result.getN();
	}

	@Override
	public int deleteByMetaId(int metaId) {
		Query query = new Query(Criteria.where(METAID_FIELD).is(metaId));
		WriteResult result = mongoTemplate.remove(query, AlarmMeta.class, ALARMMETA_COLLECTION);
		return result.getN();
	}

	@Override
	public AlarmMeta findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		AlarmMeta alarmMeta = mongoTemplate.findOne(query, AlarmMeta.class, ALARMMETA_COLLECTION);
		return alarmMeta;
	}

	@Override
	public AlarmMeta findByMetaId(int metaId) {
		Query query = new Query(Criteria.where(METAID_FIELD).is(metaId));
		AlarmMeta alarmMeta = mongoTemplate.findOne(query, AlarmMeta.class, ALARMMETA_COLLECTION);
		return alarmMeta;
	}

	@Override
	public List<AlarmMeta> findByPage(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, METAID_FIELD)));
		List<AlarmMeta> alarmMetas = mongoTemplate.find(query, AlarmMeta.class, ALARMMETA_COLLECTION);
		return alarmMetas;
	}

}
