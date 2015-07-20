package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

/**
 * 
 * @author qiyin
 *
 */
public interface GlobalAlarmSettingService {
	
	public boolean insert(GlobalAlarmSetting setting);

	public boolean update(GlobalAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByBySwallowId(String swallowId);

	public GlobalAlarmSetting findById(String id);

	public List<String> getProducerWhiteList();
	
	public List<String> getConsumerWhiteList();
	
	public GlobalAlarmSetting findBySwallowId(String swallowId);
	
	public GlobalAlarmSetting findDefault();
	
	public List<GlobalAlarmSetting> findByPage(int offset, int limit);

}
