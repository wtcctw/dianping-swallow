package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:35:10
 */
public interface GlobalAlarmSettingDao {

	boolean insert(GlobalAlarmSetting setting);

	boolean update(GlobalAlarmSetting setting);

	int deleteById(String id);

	int deleteByGlobalId(String globalId);

	GlobalAlarmSetting findById(String id);

	GlobalAlarmSetting findByGlobalId(String globalId);

	List<GlobalAlarmSetting> findByPage(int offset, int limit);
	
	long count();
}
