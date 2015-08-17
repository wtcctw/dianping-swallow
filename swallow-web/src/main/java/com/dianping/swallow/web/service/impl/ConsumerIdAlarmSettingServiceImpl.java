package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
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

	private static final String DEFAULT_CONSUMERID = "default";

	@Autowired
	private ConsumerIdAlarmSettingDao consumerIdAlarmSettingDao;

	@Override
	public boolean insert(ConsumerIdAlarmSetting setting) {
		return consumerIdAlarmSettingDao.insert(setting);
	}

	@Override
	public boolean update(ConsumerIdAlarmSetting setting) {
		ConsumerIdAlarmSetting consumerIdAlarmSetting = null;
		if (StringUtils.isNotBlank(setting.getConsumerId())) {
			consumerIdAlarmSetting = findByTopicNameAndConsumerId(setting.getTopicName(), setting.getConsumerId());
		}
		if (consumerIdAlarmSetting == null) {
			return insert(setting);
		} else {
			setting.setId(consumerIdAlarmSetting.getId());
			return consumerIdAlarmSettingDao.update(setting);
		}
	}

	@Override
	public int deleteById(String id) {
		return consumerIdAlarmSettingDao.deleteById(id);
	}

	@Override
	public int deleteByConsumerId(String consumerId) {
		return consumerIdAlarmSettingDao.deleteByConsumerId(consumerId);
	}

	@Override
	public ConsumerIdAlarmSetting findById(String id) {
		return consumerIdAlarmSettingDao.findById(id);
	}

	@Override
	public ConsumerIdAlarmSetting findDefault() {
		List<ConsumerIdAlarmSetting> consumerIdAlarmSettings = consumerIdAlarmSettingDao
				.findByConsumerId(DEFAULT_CONSUMERID);
		if (consumerIdAlarmSettings == null || consumerIdAlarmSettings.size() == 0) {
			return null;
		}
		return consumerIdAlarmSettings.get(0);
	}

	@Override
	public ConsumerIdAlarmSetting findByTopicNameAndConsumerId(String topicName, String consumerId) {
		return consumerIdAlarmSettingDao.findByTopicNameAndConsumerId(topicName, consumerId);
	}

	@Override
	public List<ConsumerIdAlarmSetting> findByPage(int offset, int limit) {
		return consumerIdAlarmSettingDao.findByPage(offset, limit);
	}

	@Override
	public List<ConsumerIdAlarmSetting> findByConsumerId(String consumerId) {
		return consumerIdAlarmSettingDao.findByConsumerId(consumerId);
	}
	
	@Override
	public long count() {
		return consumerIdAlarmSettingDao.count();
	}

}
