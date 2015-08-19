package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:44:09
 */
public interface ConsumerServerAlarmSettingService {

	boolean insert(ConsumerServerAlarmSetting setting);

	boolean update(ConsumerServerAlarmSetting setting);

	int deleteById(String id);

	int deleteByServerId(String serverId);

	ConsumerServerAlarmSetting findById(String id);

	List<String> getTopicWhiteList();

	ConsumerServerAlarmSetting findByServerId(String serverId);

	ConsumerServerAlarmSetting findDefault();

	List<ConsumerServerAlarmSetting> findByPage(int offset, int limit);
	
	long count();
}
