package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:39:12
 */
public interface TopicAlarmSettingDao {

	boolean insert(TopicAlarmSetting setting);

	boolean update(TopicAlarmSetting setting);

	int deleteById(String id);

	int deleteByTopicName(String topicName);

	TopicAlarmSetting findById(String id);

	TopicAlarmSetting findByTopicName(String topicName);

	List<TopicAlarmSetting> findByPage(int offset, int limit);

	long count();
}
