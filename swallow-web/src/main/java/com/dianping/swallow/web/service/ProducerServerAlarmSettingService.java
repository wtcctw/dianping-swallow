package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:41:12
 */
public interface ProducerServerAlarmSettingService {

	boolean insert(ProducerServerAlarmSetting setting);

	boolean update(ProducerServerAlarmSetting setting);

	int deleteById(String id);

	int deleteByServerId(String serverId);

	ProducerServerAlarmSetting findById(String id);

	List<String> getTopicWhiteList();

	ProducerServerAlarmSetting findByServerId(String serverId);

	ProducerServerAlarmSetting findDefault();

	List<ProducerServerAlarmSetting> findByPage(int offset, int limit);

	long count();
}
