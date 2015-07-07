package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;


@Service("producerServerAlarmSettingDao")
public class DefaultProducerServerAlarmSettingDao extends AbstractWriteDao implements ProducerServerAlarmSettingDao {

	@Override
	public boolean insert(ProducerServerAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(ProducerServerAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ProducerServerAlarmSetting findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ProducerServerAlarmSetting> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
