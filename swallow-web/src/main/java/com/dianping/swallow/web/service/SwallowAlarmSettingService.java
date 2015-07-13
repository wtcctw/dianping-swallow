package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.SwallowAlarmSetting;

/**
 * 
 * @author qiyin
 *
 */
public interface SwallowAlarmSettingService {
	
	public boolean insert(SwallowAlarmSetting setting);

	public boolean update(SwallowAlarmSetting setting);

	public int deleteById(String id);

	public SwallowAlarmSetting findById(String id);

	public List<SwallowAlarmSetting> findAll();
	
	public List<String> getProducerWhiteList();
	
	public List<String> getConsumerWhiteList();
	
	public SwallowAlarmSetting findBySwallowId(String swallowId);
	
	public SwallowAlarmSetting findOne();
}
