package com.dianping.swallow.web.service.impl.backup;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.dao.backup.ProducerServerAlarmSettingDao;
import com.dianping.swallow.web.model.alarm.backup.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.backup.ProducerServerAlarmSettingService;

//@Service("producerServerAlarmSettingService")
public class ProducerServerAlarmSettingServiceImpl implements ProducerServerAlarmSettingService {

	
	@Autowired
	private ProducerServerAlarmSettingDao producerServerAlarmSettingDao;
	
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
