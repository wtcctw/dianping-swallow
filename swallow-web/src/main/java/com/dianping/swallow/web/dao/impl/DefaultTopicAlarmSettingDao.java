package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.mongodb.WriteResult;

@Service("topicAlarmSettingDao")
public class DefaultTopicAlarmSettingDao extends AbstractWriteDao implements TopicAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTopicAlarmSettingDao.class);

	private static final String TOPICSALARMSETTING_COLLECTION = "swallowwebtopicalarmsettingc";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(TopicAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, TOPICSALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + setting, e);
		}
		return false;
	}

	@Override
	public boolean update(TopicAlarmSetting setting) {
		return insert(setting);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, TopicAlarmSetting.class, TOPICSALARMSETTING_COLLECTION);
		return result.getN();
	}

	@Override
	public TopicAlarmSetting findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		TopicAlarmSetting topicAlarmSetting = mongoTemplate.findOne(query, TopicAlarmSetting.class,
				TOPICSALARMSETTING_COLLECTION);
		return topicAlarmSetting;
	}

	@Override
	public List<TopicAlarmSetting> findAll() {
		return mongoTemplate.findAll(TopicAlarmSetting.class, TOPICSALARMSETTING_COLLECTION);
	}

}
