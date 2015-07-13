package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.SwallowAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.SwallowAlarmSetting;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("swallowAlarmSettingService")
public class SwallowAlarmSettingServiceImpl implements SwallowAlarmSettingService {

	@Autowired
	private SwallowAlarmSettingDao swallowAlarmSettingDao;

	@Override
	public boolean insert(SwallowAlarmSetting setting) {
		return swallowAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(SwallowAlarmSetting setting) {
		return swallowAlarmSettingDao.insert(setting);
	}

	@Override
	public int deleteById(String id) {
		return swallowAlarmSettingDao.deleteById(id);
	}
	
	@Override
	public int deleteByBySwallowId(String swallowId) {
		return swallowAlarmSettingDao.deleteByBySwallowId(swallowId);
	}


	@Override
	public SwallowAlarmSetting findById(String id) {
		return swallowAlarmSettingDao.findById(id);
	}

	@Override
	public List<SwallowAlarmSetting> findAll() {
		return swallowAlarmSettingDao.findAll();
	}

	@Override
	public List<String> getProducerWhiteList() {
		SwallowAlarmSetting swallowAlarmSetting = findOne();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getProducerWhiteList();
	}

	@Override
	public List<String> getConsumerWhiteList() {
		SwallowAlarmSetting swallowAlarmSetting = findOne();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getConsumerWhiteList();
	}

	@Override
	public SwallowAlarmSetting findOne() {
		List<SwallowAlarmSetting> swallowAlarmSettings = swallowAlarmSettingDao.findAll();
		if (swallowAlarmSettings == null || swallowAlarmSettings.size() == 0) {
			return null;
		}
		return swallowAlarmSettings.get(0);
	}

	@Override
	public SwallowAlarmSetting findBySwallowId(String swallowId) {
		return swallowAlarmSettingDao.findBySwallowId(swallowId);
	}

}
