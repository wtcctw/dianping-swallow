package com.dianping.swallow.web.service.impl.backup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.dao.backup.TopicAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.backup.TopicAlarmSetting;
import com.dianping.swallow.web.service.backup.TopicAlarmSettingService;

//@Service("topicAlarmSettingService")
public class TopicAlarmSettingServiceImpl implements TopicAlarmSettingService {

	@Autowired
	private TopicAlarmSettingDao topicAlarmSettingDao;
	
	@Override
	public boolean insert(TopicAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(TopicAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public TopicAlarmSetting findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TopicAlarmSetting findByTopic(String topicName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TopicAlarmSetting> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
