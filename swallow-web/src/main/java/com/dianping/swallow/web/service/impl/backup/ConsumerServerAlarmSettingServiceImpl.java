package com.dianping.swallow.web.service.impl.backup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.dao.backup.ConsumerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.backup.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.service.backup.ConsumerServerAlarmSettingService;

//@Service("consumerServerAlarmSettingService")
public class ConsumerServerAlarmSettingServiceImpl implements ConsumerServerAlarmSettingService {

	@Autowired
	private ConsumerServerAlarmSettingDao cconsumerServerAlarmSettingDao;

	@Override
	public boolean insert(ConsumerServerAlarmSetting setting) {
		cconsumerServerAlarmSettingDao.insert(setting);
		return false;
	}

	@Override
	public boolean update(ConsumerServerAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConsumerServerAlarmSetting findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConsumerServerAlarmSetting> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
