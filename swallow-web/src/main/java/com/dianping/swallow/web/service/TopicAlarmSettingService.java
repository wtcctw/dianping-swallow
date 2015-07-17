package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

/**
*
* @author qiyin
*
*/
public interface TopicAlarmSettingService {

	public boolean insert(TopicAlarmSetting setting);

	public boolean update(TopicAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByTopicName(String topicName);

	public TopicAlarmSetting findById(String id);
	
	public TopicAlarmSetting findDefault();
	
	public TopicAlarmSetting findByTopicName(String topicName);
	
	public List<String> getConsumerIdWhiteList();
	
	public List<TopicAlarmSetting> findByPage(int offset, int limit);

	
}
