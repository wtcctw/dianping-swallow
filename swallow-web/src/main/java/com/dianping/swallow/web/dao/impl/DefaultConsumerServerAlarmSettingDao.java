package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;

@Service("consumerServerAlarmSettingDao")
public class DefaultConsumerServerAlarmSettingDao extends AbstractWriteDao implements ConsumerServerAlarmSettingDao {

	@Override
	public boolean insert(ConsumerServerAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ConsumerServerAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ConsumerServerAlarmSetting findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ConsumerServerAlarmSetting> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
