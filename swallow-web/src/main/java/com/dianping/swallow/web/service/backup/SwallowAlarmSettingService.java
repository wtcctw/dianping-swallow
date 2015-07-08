package com.dianping.swallow.web.service.backup;

import java.util.List;

import com.dianping.swallow.web.model.alarm.backup.SwallowAlarmSetting;

public interface SwallowAlarmSettingService {
	public boolean insert(SwallowAlarmSetting setting);
	
	public boolean update(SwallowAlarmSetting setting);
	
	public int deleteById(String id);
	
	public SwallowAlarmSetting findById(String id);
	
	public List<SwallowAlarmSetting> findAll();
}
