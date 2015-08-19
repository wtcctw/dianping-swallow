package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.GlobalAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("globalAlarmSettingService")
public class GlobalAlarmSettingServiceImpl implements GlobalAlarmSettingService {

	private static final String DEFAULT_GLOBALID = "default";

	@Autowired
	private GlobalAlarmSettingDao globalAlarmSettingDao;

	@Override
	public boolean insert(GlobalAlarmSetting setting) {
		return globalAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(GlobalAlarmSetting setting) {
		GlobalAlarmSetting swallowAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getSwallowId())) {
			swallowAlarmSetting = findByGlobalId(setting.getSwallowId());
		}
		if (swallowAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(swallowAlarmSetting.getId());
			return globalAlarmSettingDao.update(setting);
		}
	}

	@Override
	public int deleteById(String id) {
		return globalAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByBySwallowId(String globalId) {
		return globalAlarmSettingDao.deleteByGlobalId(globalId);
	}

	@Override
	public GlobalAlarmSetting findById(String id) {
		return globalAlarmSettingDao.findById(id);
	}

	@Override
	public List<String> getProducerWhiteList() {
		GlobalAlarmSetting swallowAlarmSetting = findDefault();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getProducerWhiteList();
	}

	@Override
	public List<String> getConsumerWhiteList() {
		GlobalAlarmSetting swallowAlarmSetting = findDefault();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getConsumerWhiteList();
	}

	@Override
	public GlobalAlarmSetting findDefault() {
		GlobalAlarmSetting swallowAlarmSetting = globalAlarmSettingDao.findByGlobalId(DEFAULT_GLOBALID);
		return swallowAlarmSetting;
	}

	@Override
	public GlobalAlarmSetting findByGlobalId(String globalId) {
		return globalAlarmSettingDao.findByGlobalId(globalId);
	}

	@Override
	public List<GlobalAlarmSetting> findByPage(int offset, int limit) {
		return globalAlarmSettingDao.findByPage(offset, limit);
	}
	
	@Override
	public long count() {
		return globalAlarmSettingDao.count();
	}
}
