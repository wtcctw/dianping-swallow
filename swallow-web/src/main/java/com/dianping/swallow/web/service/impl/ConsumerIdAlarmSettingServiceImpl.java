package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIdAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;

/**
*
* @author qiyin
*
*/
@Service("consumerIdAlarmSettingService")
public class ConsumerIdAlarmSettingServiceImpl implements ConsumerIdAlarmSettingService {

	@Autowired
	private ConsumerIdAlarmSettingDao consumerIdAlarmSettingDao;

	@Override
	public boolean insert(ConsumerIdAlarmSetting setting) {
		return consumerIdAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(ConsumerIdAlarmSetting setting) {
		return consumerIdAlarmSettingDao.update(setting);
	}

	@Override
	public int deleteById(String id) {
		return consumerIdAlarmSettingDao.deleteById(id);
	}

	@Override
	public ConsumerIdAlarmSetting findById(String id) {
		return consumerIdAlarmSettingDao.findById(id);
	}

	@Override
	public List<ConsumerIdAlarmSetting> findAll() {
		return consumerIdAlarmSettingDao.findAll();
	}

	@Override
	public ConsumerIdAlarmSetting findOne() {
		List<ConsumerIdAlarmSetting> consumerIdAlarmSettings = findAll();
		if (consumerIdAlarmSettings == null || consumerIdAlarmSettings.size() == 0) {
			return null;
		}
		return consumerIdAlarmSettings.get(0);
	}

	@Override
	public ConsumerIdAlarmSetting findByConsumerId(String consumerId) {
		return consumerIdAlarmSettingDao.findByConsumerId(consumerId);
	}

}
