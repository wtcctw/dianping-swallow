package com.dianping.swallow.web.service.backup;

import java.util.List;

import com.dianping.swallow.web.model.alarm.backup.TopicAlarmSetting;

public interface TopicAlarmSettingService {
	public boolean insert(TopicAlarmSetting setting);

	public boolean update(TopicAlarmSetting setting);

	public int deleteById(String id);

	public TopicAlarmSetting findById(String id);
	
	public TopicAlarmSetting findByTopic(String topicName);

	public List<TopicAlarmSetting> findAll();
}
