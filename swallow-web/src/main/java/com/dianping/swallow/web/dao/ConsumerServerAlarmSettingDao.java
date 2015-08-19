package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:34:16
 */
public interface ConsumerServerAlarmSettingDao {

	boolean insert(ConsumerServerAlarmSetting setting);

	boolean update(ConsumerServerAlarmSetting setting);

	int deleteById(String id);

	int deleteByServerId(String serverId);

	ConsumerServerAlarmSetting findById(String id);

	ConsumerServerAlarmSetting findByServerId(String serverId);

	List<ConsumerServerAlarmSetting> findByPage(int offset, int limit);

	long count();
}
