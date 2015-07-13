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

	public TopicAlarmSetting findById(String id);
	
	public List<TopicAlarmSetting> findAll();

	public TopicAlarmSetting findOne();
	
	public List<String> getConsumerIdWhiteList();
	
}
