package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("producerServerAlarmSettingDao")
public class DefaultProducerServerAlarmSettingDao extends AbstractWriteDao implements ProducerServerAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServerAlarmSettingDao.class);

	private static final String PRODUCERSERVERALARMSETTING_COLLECTION = "swallowwebproducerserveralarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String SERVERID_FIELD = "serverId";

	@Override
	public boolean insert(ProducerServerAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, PRODUCERSERVERALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save producer server alarm setting " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerServerAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerServerAlarmSetting.class,
				PRODUCERSERVERALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public int deleteByServerId(String serverId) {
		Query query = new Query(Criteria.where(SERVERID_FIELD).is(serverId));
		WriteResult result = mongoTemplate.remove(query, ProducerServerAlarmSetting.class,
				PRODUCERSERVERALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerServerAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerServerAlarmSetting serverAlarmSetting = mongoTemplate.findOne(query, ProducerServerAlarmSetting.class,
				PRODUCERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSetting;
	}

	@Override
	public ProducerServerAlarmSetting findByServerId(String serverId) {
		Query query = new Query(Criteria.where(SERVERID_FIELD).is(serverId));
		ProducerServerAlarmSetting serverAlarmSetting = mongoTemplate.findOne(query, ProducerServerAlarmSetting.class,
				PRODUCERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSetting;
	}

	@Override
	public List<ProducerServerAlarmSetting> findByPage(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, SERVERID_FIELD)));
		List<ProducerServerAlarmSetting> serverAlarmSettings = mongoTemplate.find(query,
				ProducerServerAlarmSetting.class, PRODUCERSERVERALARMSETTING_COLLECTION);
		return serverAlarmSettings;
	}

}
