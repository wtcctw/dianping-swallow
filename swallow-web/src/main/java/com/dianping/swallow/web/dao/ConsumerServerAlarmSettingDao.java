package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

/**
*
* @author qiyin
*
*/
public interface ConsumerServerAlarmSettingDao {
	
	public boolean insert(ConsumerServerAlarmSetting setting);

	public boolean update(ConsumerServerAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByServerId(String serverId);

	public ConsumerServerAlarmSetting findById(String id);
	
	public ConsumerServerAlarmSetting findByServerId(String serverId);

	public List<ConsumerServerAlarmSetting> findAll();
	
}
