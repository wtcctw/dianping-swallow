package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
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
		ConsumerServerAlarmSetting serverAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getServerId())) {
			serverAlarmSetting = findByServerId(setting.getServerId());
		}
		if (serverAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(serverAlarmSetting.getId());
			return consumerServerAlarmSettingDao.update(setting);
		}
	}

	@Override
	public int deleteById(String id) {
		return consumerServerAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByServerId(String serverId) {
		return consumerServerAlarmSettingDao.deleteByServerId(serverId);
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
	public List<String> getTopicWhiteList() {
		ConsumerServerAlarmSetting serverAlarmSetting = findOne();
		if (serverAlarmSetting == null) {
			return null;
		}
		return serverAlarmSetting.getTopicWhiteList();
	}

	@Override
	public ConsumerServerAlarmSetting findOne() {
		List<ConsumerServerAlarmSetting> serverAlarmSettings = findAll();
		if (serverAlarmSettings == null || serverAlarmSettings.size() == 0) {
			return null;
		}
		return serverAlarmSettings.get(0);
	}

	@Override
	public ConsumerServerAlarmSetting findByServerId(String serverId) {
		return consumerServerAlarmSettingDao.findByServerId(serverId);
	}

}
