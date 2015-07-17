package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("topicAlarmSettingDao")
public class DefaultTopicAlarmSettingDao extends AbstractWriteDao implements TopicAlarmSettingDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultTopicAlarmSettingDao.class);

	private static final String TOPICSALARMSETTING_COLLECTION = "swallowwebtopicalarmsettingc";

	private static final String ID_FIELD = "id";

	private static final String TOPICNAME_FIELD = "topicName";

	@Override
	public boolean insert(TopicAlarmSetting setting) {
		try {
			mongoTemplate.save(setting, TOPICSALARMSETTING_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic alarm setting " + setting, e);
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
	public int deleteByTopicName(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
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
	public TopicAlarmSetting findByTopicName(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		TopicAlarmSetting topicAlarmSetting = mongoTemplate.findOne(query, TopicAlarmSetting.class,
				TOPICSALARMSETTING_COLLECTION);
		return topicAlarmSetting;
	}

	@Override
	public List<TopicAlarmSetting> findByPage(int offset, int limit) {
		Query query = new Query();
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPICNAME_FIELD)));
		List<TopicAlarmSetting> topicAlarmSettings = mongoTemplate.find(query, TopicAlarmSetting.class,
				TOPICSALARMSETTING_COLLECTION);
		return topicAlarmSettings;
	}

}
