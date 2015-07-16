package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

/**
*
* @author qiyin
*
*/
public interface ConsumerServerAlarmSettingService {
	
	public boolean insert(ConsumerServerAlarmSetting setting);

	public boolean update(ConsumerServerAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByServerId(String serverId);

	public ConsumerServerAlarmSetting findById(String id);

	public List<ConsumerServerAlarmSetting> findAll();
	
	public List<String> getTopicWhiteList();
	
	public ConsumerServerAlarmSetting findByServerId(String serverId);

	public ConsumerServerAlarmSetting findOne();
	
}
