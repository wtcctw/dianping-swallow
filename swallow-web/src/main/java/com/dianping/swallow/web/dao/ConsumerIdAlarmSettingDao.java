package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:31:34
 */
public interface ConsumerIdAlarmSettingDao {

	boolean insert(ConsumerIdAlarmSetting setting);

	boolean update(ConsumerIdAlarmSetting setting);

	int deleteById(String id);

	int deleteByConsumerId(String consumerId);

	ConsumerIdAlarmSetting findById(String id);

	ConsumerIdAlarmSetting findByTopicNameAndConsumerId(String topicName, String consumerId);

	List<ConsumerIdAlarmSetting> findByConsumerId(String consumerId);

	List<ConsumerIdAlarmSetting> findByPage(int offset, int limit);

	long count();
}
