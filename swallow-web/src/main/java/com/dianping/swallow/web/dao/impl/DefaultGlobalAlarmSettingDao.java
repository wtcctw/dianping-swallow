package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.GlobalAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("globalAlarmSettingDao")
public class DefaultGlobalAlarmSettingDao extends AbstractWriteDao implements GlobalAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultGlobalAlarmSettingDao.class);

	private static final String SWALLOWALARMSETTING_COLLECTION = "swallowwebswallowalarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String SWALLOWID_FIELD = "swallowId";

	@Override
	public boolean insert(GlobalAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, SWALLOWALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save swallow alarm setting " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(GlobalAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, GlobalAlarmSetting.class, SWALLOWALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public int deleteByGlobalId(String globalId) {
		Query query = new Query(Criteria.where(SWALLOWID_FIELD).is(globalId));
		WriteResult result = mongoTemplate.remove(query, GlobalAlarmSetting.class, SWALLOWALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public GlobalAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		GlobalAlarmSetting swallowAlarmSetting = mongoTemplate.findOne(query, GlobalAlarmSetting.class,
				SWALLOWALARMSETTING_COLLECTION);
		return swallowAlarmSetting;
	}

	@Override
	public GlobalAlarmSetting findByGlobalId(String globalId) {
		Query query = new Query(Criteria.where(SWALLOWID_FIELD).is(globalId));
		GlobalAlarmSetting swallowAlarmSetting = mongoTemplate.findOne(query, GlobalAlarmSetting.class,
				SWALLOWALARMSETTING_COLLECTION);
		return swallowAlarmSetting;
	}

	@Override
	public List<GlobalAlarmSetting> findByPage(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, SWALLOWID_FIELD)));
		List<GlobalAlarmSetting> swallowAlarmSettings = mongoTemplate.find(query, GlobalAlarmSetting.class,
				SWALLOWALARMSETTING_COLLECTION);
		return swallowAlarmSettings;
	}

	@Override
	public long count() {
		Query query = new Query();
		return mongoTemplate.count(query, SWALLOWALARMSETTING_COLLECTION);
	}

}
