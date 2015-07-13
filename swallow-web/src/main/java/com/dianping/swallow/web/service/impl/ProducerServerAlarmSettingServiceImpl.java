package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

/**
*
* @author qiyin
*
*/
@Service("producerServerAlarmSettingService")
public class ProducerServerAlarmSettingServiceImpl implements ProducerServerAlarmSettingService {

	@Autowired
	private ProducerServerAlarmSettingDao producerServerAlarmSettingDao;

	@Override
	public boolean insert(ProducerServerAlarmSetting setting) {
		return producerServerAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(ProducerServerAlarmSetting setting) {
		return producerServerAlarmSettingDao.update(setting);
	}

	@Override
	public int deleteById(String id) {
		return producerServerAlarmSettingDao.deleteById(id);
	}

	@Override
	public ProducerServerAlarmSetting findById(String id) {
		return producerServerAlarmSettingDao.findById(id);
	}

	@Override
	public List<ProducerServerAlarmSetting> findAll() {
		return producerServerAlarmSettingDao.findAll();
	}

	@Override
	public List<String> getTopicWhiteList() {
		ProducerServerAlarmSetting serverAlarmSetting = findOne();
		return serverAlarmSetting.getTopicWhiteList();
	}
	
	@Override
	public ProducerServerAlarmSetting findOne(){
		List<ProducerServerAlarmSetting> serverAlarmSettings = findAll();
		if (serverAlarmSettings == null || serverAlarmSettings.size() == 0) {
			return null;
		}
		return serverAlarmSettings.get(0);
	}

	@Override
	public ProducerServerAlarmSetting findByServerId(String serverId) {
		return producerServerAlarmSettingDao.findByServerId(serverId);
	}

}
