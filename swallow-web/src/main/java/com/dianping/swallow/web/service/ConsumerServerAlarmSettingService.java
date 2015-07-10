package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

public interface ConsumerServerAlarmSettingService {
	
	public boolean insert(ConsumerServerAlarmSetting setting);

	public boolean update(ConsumerServerAlarmSetting setting);

	public int deleteById(String id);

	public ConsumerServerAlarmSetting findById(String id);

	public List<ConsumerServerAlarmSetting> findAll();
	
	public List<String> getWhiteList();

	ConsumerServerAlarmSetting findOne();
	
}
