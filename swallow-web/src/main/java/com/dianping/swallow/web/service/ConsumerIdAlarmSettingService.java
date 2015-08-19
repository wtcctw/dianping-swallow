package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:46:32
 */
public interface ConsumerIdAlarmSettingService {

	boolean insert(ConsumerIdAlarmSetting setting);

	boolean update(ConsumerIdAlarmSetting setting);

	int deleteById(String id);

	int deleteByConsumerId(String consumerId);

	ConsumerIdAlarmSetting findById(String id);

	List<ConsumerIdAlarmSetting> findByConsumerId(String consumerId);

	ConsumerIdAlarmSetting findDefault();

	List<ConsumerIdAlarmSetting> findByPage(int offset, int limit);

	ConsumerIdAlarmSetting findByTopicNameAndConsumerId(String topicName, String consumerId);

	long count();
}
