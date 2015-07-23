package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

/**
 * 
 * @author qiyin
 *
 */
public interface GlobalAlarmSettingDao {

	public boolean insert(GlobalAlarmSetting setting);

	public boolean update(GlobalAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByBySwallowId(String swallowId);

	public GlobalAlarmSetting findById(String id);
	
	public GlobalAlarmSetting findBySwallowId(String swallowId);

	public List<GlobalAlarmSetting> findByPage(int offset, int limit);
}
