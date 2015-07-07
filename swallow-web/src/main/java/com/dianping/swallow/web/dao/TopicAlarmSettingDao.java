package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

public interface TopicAlarmSettingDao {

	public boolean insert(TopicAlarmSetting setting);

	public boolean update(TopicAlarmSetting setting);

	public int deleteById(String id);

	public TopicAlarmSetting findById(String id);
	
	public TopicAlarmSetting findByTopic(String topicName);

	public List<TopicAlarmSetting> findAll();
}
