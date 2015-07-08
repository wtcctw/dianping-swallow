package com.dianping.swallow.web.dao.backup;

import java.util.List;

import com.dianping.swallow.web.model.alarm.backup.ConsumerServerAlarmSetting;

public interface ConsumerServerAlarmSettingDao {
	
	public boolean insert(ConsumerServerAlarmSetting setting);

	public boolean update(ConsumerServerAlarmSetting setting);

	public int deleteById(String id);

	public ConsumerServerAlarmSetting findById(String id);

	public List<ConsumerServerAlarmSetting> findAll();
	
}
