package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerServerAlarmSettingDao")
public class DefaultConsumerServerAlarmSettingDao extends AbstractWriteDao implements ConsumerServerAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerServerAlarmSettingDao.class);

	private static final String CONSUMERSERVERALARMSETTING_COLLECTION = "swallowwebconsumerserveralarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String SERVERID_FIELD = "serverId";

	@Override
	public boolean insert(ConsumerServerAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, CONSUMERSERVERALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save consumer server alarm setting " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(ConsumerServerAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ConsumerServerAlarmSetting.class,
				CONSUMERSERVERALARMSETTING_COLLECTION);
		return result.getN();
	}
	
	@Override
	public int deleteByServerId(String serverId) {
		Query query = new Query(Criteria.where(SERVERID_FIELD).is(serverId));
		WriteResult result = mongoTemplate.remove(query, ConsumerServerAlarmSetting.class,
				CONSUMERSERVERALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public ConsumerServerAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ConsumerServerAlarmSetting serverAlarmSetting = mongoTemplate.findOne(query, ConsumerServerAlarmSetting.class,
				CONSUMERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSetting;
	}

	@Override
	public ConsumerServerAlarmSetting findByServerId(String serverId) {
		Query query = new Query(Criteria.where(SERVERID_FIELD).is(serverId));
		ConsumerServerAlarmSetting serverAlarmSetting = mongoTemplate.findOne(query, ConsumerServerAlarmSetting.class,
				CONSUMERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSetting;
	}

	@Override
	public List<ConsumerServerAlarmSetting> findByPage(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, SERVERID_FIELD)));
		List<ConsumerServerAlarmSetting> serverAlarmSettings = mongoTemplate.find(query,
				ConsumerServerAlarmSetting.class, CONSUMERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSettings;
	}
}
