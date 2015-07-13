package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerIdAlarmSettingDao")
public class DefaultConsumerIdAlarmSettingDao extends AbstractWriteDao implements ConsumerIdAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerIdAlarmSettingDao.class);

	private static final String CONSUMERIDALARMSETTING_COLLECTION = "swallowwebconsumeridalarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String CONSUMERID_FIELD = "consumerId";

	@Override
	public boolean insert(ConsumerIdAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, CONSUMERIDALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save consumerid alarmsetting " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerIdAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerIdAlarmSetting.class,
				CONSUMERIDALARMSETTING_COLLECTION);
		return result.getN();
	}
	
	@Override
	public int deleteByConsumerId(String consumerId) {
		Query query = new Query(Criteria.where(CONSUMERID_FIELD).is(consumerId));
		WriteResult result = mongoTemplate.remove(query, ConsumerIdAlarmSetting.class,
				CONSUMERIDALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerIdAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerIdAlarmSetting alarmSetting = mongoTemplate.findOne(query, ConsumerIdAlarmSetting.class,
				CONSUMERIDALARMSETTING_COLLECTION);
		return alarmSetting;
	}

	@Override
	public List<ConsumerIdAlarmSetting> findAll() {
		return mongoTemplate.findAll(ConsumerIdAlarmSetting.class, CONSUMERIDALARMSETTING_COLLECTION);
	}

	@Override
	public ConsumerIdAlarmSetting findByConsumerId(String consumerId) {
		Query query = new Query(Criteria.where(CONSUMERID_FIELD).is(consumerId));
		ConsumerIdAlarmSetting alarmSetting = mongoTemplate.findOne(query, ConsumerIdAlarmSetting.class,
				CONSUMERIDALARMSETTING_COLLECTION);
		return alarmSetting;
	}

}
