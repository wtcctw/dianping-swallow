package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

	private static final String DEFAULT_SERVERID = "default";

	@Autowired
	private ProducerServerAlarmSettingDao producerServerAlarmSettingDao;

	@Override
	public boolean insert(ProducerServerAlarmSetting setting) {
		return producerServerAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(ProducerServerAlarmSetting setting) {
		ProducerServerAlarmSetting serverAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getServerId())) {
			serverAlarmSetting = findByServerId(setting.getServerId());
		}
		if (serverAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(serverAlarmSetting.getId());
			return producerServerAlarmSettingDao.update(setting);
		}
	}

	@Override
	public int deleteById(String id) {
		return producerServerAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByServerId(String serverId) {
		return producerServerAlarmSettingDao.deleteByServerId(serverId);
	}

	@Override
	public ProducerServerAlarmSetting findById(String id) {
		return producerServerAlarmSettingDao.findById(id);
	}

	@Override
	public List<String> getTopicWhiteList() {
		ProducerServerAlarmSetting serverAlarmSetting = findDefault();
		if (serverAlarmSetting == null) {
			return null;
		}
		return serverAlarmSetting.getTopicWhiteList();
	}

	@Override
	public ProducerServerAlarmSetting findDefault() {
		ProducerServerAlarmSetting serverAlarmSetting = producerServerAlarmSettingDao.findByServerId(DEFAULT_SERVERID);
		return serverAlarmSetting;
	}

	@Override
	public ProducerServerAlarmSetting findByServerId(String serverId) {
		return producerServerAlarmSettingDao.findByServerId(serverId);
	}

	@Override
	public List<ProducerServerAlarmSetting> findByPage(int offset, int limit) {
		return producerServerAlarmSettingDao.findByPage(offset, limit);
	}

}
