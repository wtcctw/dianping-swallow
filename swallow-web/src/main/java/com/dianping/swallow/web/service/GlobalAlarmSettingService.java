package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:43:19
 */
public interface GlobalAlarmSettingService {

	boolean insert(GlobalAlarmSetting setting);

	boolean update(GlobalAlarmSetting setting);

	int deleteById(String id);

	int deleteByBySwallowId(String globalId);

	GlobalAlarmSetting findById(String id);

	List<String> getProducerWhiteList();

	List<String> getConsumerWhiteList();

	GlobalAlarmSetting findByGlobalId(String globalId);

	GlobalAlarmSetting findDefault();

	List<GlobalAlarmSetting> findByPage(int offset, int limit);

	long count();
}
