package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("topicAlarmSettingService")
public class TopicAlarmSettingServiceImpl implements TopicAlarmSettingService {

	@Autowired
	private TopicAlarmSettingDao topicAlarmSettingDao;

	@Override
	public boolean insert(TopicAlarmSetting setting) {
		return topicAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(TopicAlarmSetting setting) {
		return topicAlarmSettingDao.update(setting);
	}

	@Override
	public int deleteById(String id) {
		return topicAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByTopicName(String topicName) {
		return topicAlarmSettingDao.deleteByTopicName(topicName);
	}

	@Override
	public TopicAlarmSetting findById(String id) {
		return topicAlarmSettingDao.findById(id);
	}

	@Override
	public List<TopicAlarmSetting> findAll() {
		return topicAlarmSettingDao.findAll();
	}

	@Override
	public List<String> getConsumerIdWhiteList() {
		TopicAlarmSetting topicAlarmSetting = findOne();
		if (topicAlarmSetting == null) {
			return null;
		}
		return topicAlarmSetting.getConsumerIdWhiteList();
	}

	@Override
	public TopicAlarmSetting findOne() {
		List<TopicAlarmSetting> topicAlarmSetting = topicAlarmSettingDao.findAll();
		if (topicAlarmSetting == null || topicAlarmSetting.size() == 0) {
			return null;
		}
		return topicAlarmSetting.get(0);
	}

	@Override
	public TopicAlarmSetting findByTopicName(String topicName) {
		return topicAlarmSettingDao.findByTopicName(topicName);
	}

}
