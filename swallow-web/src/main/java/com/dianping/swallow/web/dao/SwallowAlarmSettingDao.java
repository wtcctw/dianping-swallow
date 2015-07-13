package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.SwallowAlarmSetting;

/**
 * 
 * @author qiyin
 *
 */
public interface SwallowAlarmSettingDao {

	public boolean insert(SwallowAlarmSetting setting);

	public boolean update(SwallowAlarmSetting setting);

	public int deleteById(String id);

	public SwallowAlarmSetting findById(String id);

	public List<SwallowAlarmSetting> findAll();

}
