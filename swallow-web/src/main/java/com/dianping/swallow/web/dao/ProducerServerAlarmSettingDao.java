package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;

/**
*
* @author qiyin
*
*/
public interface ProducerServerAlarmSettingDao {

	public boolean insert(ProducerServerAlarmSetting setting);

	public boolean update(ProducerServerAlarmSetting setting);

	public int deleteById(String id);
	
	public int deleteByServerId(String serverId);

	public ProducerServerAlarmSetting findById(String id);
	
	public ProducerServerAlarmSetting findByServerId(String serverId);

	public List<ProducerServerAlarmSetting> findAll();
	
}
