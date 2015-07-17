package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
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

	private static final String DEFAULT_SWALLOWID = "default";

	@Autowired
	private SwallowAlarmSettingDao swallowAlarmSettingDao;

	@Override
	public boolean insert(SwallowAlarmSetting setting) {
		return swallowAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(SwallowAlarmSetting setting) {
		SwallowAlarmSetting swallowAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getSwallowId())) {
			swallowAlarmSetting = findBySwallowId(setting.getSwallowId());
		}
		if (swallowAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(swallowAlarmSetting.getId());
			return swallowAlarmSettingDao.update(setting);
		}
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
	public List<String> getProducerWhiteList() {
		SwallowAlarmSetting swallowAlarmSetting = findDefault();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getProducerWhiteList();
	}

	@Override
	public List<String> getConsumerWhiteList() {
		SwallowAlarmSetting swallowAlarmSetting = findDefault();
		if (swallowAlarmSetting == null) {
			return null;
		}
		return swallowAlarmSetting.getConsumerWhiteList();
	}

	@Override
	public SwallowAlarmSetting findDefault() {
		SwallowAlarmSetting swallowAlarmSetting = swallowAlarmSettingDao.findBySwallowId(DEFAULT_SWALLOWID);
		return swallowAlarmSetting;
	}

	@Override
	public SwallowAlarmSetting findBySwallowId(String swallowId) {
		return swallowAlarmSettingDao.findBySwallowId(swallowId);
	}

	@Override
	public List<SwallowAlarmSetting> findByPage(int offset, int limit) {
		return swallowAlarmSettingDao.findByPage(offset, limit);
	}

}
