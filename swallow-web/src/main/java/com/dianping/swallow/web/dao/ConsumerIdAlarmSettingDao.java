package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;

public interface ConsumerIdAlarmSettingDao {

	public boolean insert(ConsumerIdAlarmSetting setting);

	public boolean update(ConsumerIdAlarmSetting setting);

	public int deleteById(String id);

	public ConsumerIdAlarmSetting findById(String id);

	public List<ConsumerIdAlarmSetting> findAll();
}
