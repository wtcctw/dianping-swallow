package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("topicAlarmSettingService")
public class TopicAlarmSettingServiceImpl implements TopicAlarmSettingService {

	private static final String DEFAULT_TOPICNAME = "default";

	@Autowired
	private TopicAlarmSettingDao topicAlarmSettingDao;

	@Override
	public boolean insert(TopicAlarmSetting setting) {
		return topicAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(TopicAlarmSetting setting) {
		TopicAlarmSetting topicAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getTopicName())) {
			topicAlarmSetting = findByTopicName(setting.getTopicName());
		}
		if (topicAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(topicAlarmSetting.getId());
			return topicAlarmSettingDao.update(setting);
		}
	}

	@Override
	public int deleteById(String id) {
		return topicAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByTopicName(String topicName) {
		return topicAlarmSettingDao.deleteByTopicName(topicName);
	}

	@Override
	public TopicAlarmSetting findById(String id) {
		return topicAlarmSettingDao.findById(id);
	}

	@Override
	public List<String> getConsumerIdWhiteList() {
		TopicAlarmSetting topicAlarmSetting = findDefault();
		if (topicAlarmSetting == null) {
			return null;
		}
		return topicAlarmSetting.getConsumerIdWhiteList();
	}

	@Override
	public TopicAlarmSetting findDefault() {
		TopicAlarmSetting topicAlarmSetting = topicAlarmSettingDao.findByTopicName(DEFAULT_TOPICNAME);
		return topicAlarmSetting;
	}

	@Override
	public TopicAlarmSetting findByTopicName(String topicName) {
		return topicAlarmSettingDao.findByTopicName(topicName);
	}

	@Override
	public List<TopicAlarmSetting> findByPage(int offset, int limit) {
		return topicAlarmSettingDao.findByPage(offset, limit);
	}

}
