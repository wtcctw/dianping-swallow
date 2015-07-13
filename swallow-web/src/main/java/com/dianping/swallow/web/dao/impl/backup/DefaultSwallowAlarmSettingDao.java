package com.dianping.swallow.web.dao.impl.backup;

import java.util.List;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.backup.SwallowAlarmSettingDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.alarm.backup.SwallowAlarmSetting;


//@Service("swallowAlarmSettingDao")
public class DefaultSwallowAlarmSettingDao extends AbstractWriteDao implements SwallowAlarmSettingDao {

	@Override
	public boolean insert(SwallowAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(SwallowAlarmSetting setting) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int deleteById(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SwallowAlarmSetting findById(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SwallowAlarmSetting> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}
