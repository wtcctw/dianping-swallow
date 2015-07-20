package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
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

	private static final String DEFAULT_SWALLOWID = "default";

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
			swallowAlarmSetting = findBySwallowId(setting.getSwallowId());
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
	public int deleteByBySwallowId(String swallowId) {
		return globalAlarmSettingDao.deleteByBySwallowId(swallowId);
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
		GlobalAlarmSetting swallowAlarmSetting = globalAlarmSettingDao.findBySwallowId(DEFAULT_SWALLOWID);
		return swallowAlarmSetting;
	}

	@Override
	public GlobalAlarmSetting findBySwallowId(String swallowId) {
		return globalAlarmSettingDao.findBySwallowId(swallowId);
	}

	@Override
	public List<GlobalAlarmSetting> findByPage(int offset, int limit) {
		return globalAlarmSettingDao.findByPage(offset, limit);
	}

}
