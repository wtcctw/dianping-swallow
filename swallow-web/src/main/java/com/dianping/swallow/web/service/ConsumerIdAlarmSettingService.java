package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;

/**
*
* @author qiyin
*
*/
public interface ConsumerIdAlarmSettingService {

	public boolean insert(ConsumerIdAlarmSetting setting);

	public boolean update(ConsumerIdAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByConsumerId(String consumerId);

	public ConsumerIdAlarmSetting findById(String id);

	public List<ConsumerIdAlarmSetting> findAll();

	public ConsumerIdAlarmSetting findByConsumerId(String consumerId);
	
	public ConsumerIdAlarmSetting findOne();
}
