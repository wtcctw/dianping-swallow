package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;

/**
*
* @author qiyin
*
*/
@Service("consumerServerAlarmSettingService")
public class ConsumerServerAlarmSettingServiceImpl implements ConsumerServerAlarmSettingService {

	@Autowired
	private ConsumerServerAlarmSettingDao consumerServerAlarmSettingDao;

	@Override
	public boolean insert(ConsumerServerAlarmSetting setting) {
		return consumerServerAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(ConsumerServerAlarmSetting setting) {
		return consumerServerAlarmSettingDao.update(setting);
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConsumerServerAlarmSetting findById(String id) {
		return consumerServerAlarmSettingDao.findById(id);
	}

	@Override
	public List<ConsumerServerAlarmSetting> findAll() {
		return consumerServerAlarmSettingDao.findAll();
	}

	@Override
	public List<String> getWhiteList() {
		List<ConsumerServerAlarmSetting> serverAlarmSettings = findAll();
		if (serverAlarmSettings == null || serverAlarmSettings.size() == 0) {
			return null;
		}
		ConsumerServerAlarmSetting serverAlarmSetting = serverAlarmSettings.get(0);
		return serverAlarmSetting.getWhiteList();
	}
	
	@Override
	public ConsumerServerAlarmSetting findOne(){
		List<ConsumerServerAlarmSetting> serverAlarmSettings = findAll();
		if (serverAlarmSettings == null || serverAlarmSettings.size() == 0) {
			return null;
		}
		return serverAlarmSettings.get(0);
	}

}
