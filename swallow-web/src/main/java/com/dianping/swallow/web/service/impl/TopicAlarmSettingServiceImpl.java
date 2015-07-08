package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

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
	public TopicAlarmSetting findById(String id) {
		return topicAlarmSettingDao.findById(id);
	}

	@Override
	public List<TopicAlarmSetting> findAll() {
		return topicAlarmSettingDao.findAll();
	}

}
