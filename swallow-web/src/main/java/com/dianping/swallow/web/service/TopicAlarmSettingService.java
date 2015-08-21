package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:39:55
 */
public interface TopicAlarmSettingService {

	boolean insert(TopicAlarmSetting setting);

	boolean update(TopicAlarmSetting setting);

	int deleteById(String id);

	int deleteByTopicName(String topicName);

	TopicAlarmSetting findById(String id);

	TopicAlarmSetting findDefault();

	TopicAlarmSetting findByTopicName(String topicName);

	List<String> getConsumerIdWhiteList();

	List<TopicAlarmSetting> findByPage(int offset, int limit);

	long count();
}
