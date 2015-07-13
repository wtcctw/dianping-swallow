package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.SwallowAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.SwallowAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("swallowAlarmSettingDao")
public class DefaultSwallowAlarmSettingDao extends AbstractWriteDao implements SwallowAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultSwallowAlarmSettingDao.class);

	private static final String SWALLOWALARMSETTING_COLLECTION = "swallowwebswallowalarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String SWALLOWID_FIELD = "swallowId";

	@Override
	public boolean insert(SwallowAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, SWALLOWALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save swallow alarm setting " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(SwallowAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, SwallowAlarmSetting.class, SWALLOWALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public SwallowAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		SwallowAlarmSetting swallowAlarmSetting = mongoTemplate.findOne(query, SwallowAlarmSetting.class,
				SWALLOWALARMSETTING_COLLECTION);
		return swallowAlarmSetting;
	}

	@Override
	public List<SwallowAlarmSetting> findAll() {
		return mongoTemplate.findAll(SwallowAlarmSetting.class, SWALLOWALARMSETTING_COLLECTION);
	}

	@Override
	public SwallowAlarmSetting findBySwallowId(String swallowId) {
		Query query = new Query(Criteria.where(SWALLOWID_FIELD).is(swallowId));
		SwallowAlarmSetting swallowAlarmSetting = mongoTemplate.findOne(query, SwallowAlarmSetting.class,
				SWALLOWALARMSETTING_COLLECTION);
		return swallowAlarmSetting;
	}

}
