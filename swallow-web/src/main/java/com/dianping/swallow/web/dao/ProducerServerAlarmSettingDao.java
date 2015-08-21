package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:36:51
 */
public interface ProducerServerAlarmSettingDao {

	boolean insert(ProducerServerAlarmSetting setting);

	boolean update(ProducerServerAlarmSetting setting);

	int deleteById(String id);

	int deleteByServerId(String serverId);

	ProducerServerAlarmSetting findById(String id);

	ProducerServerAlarmSetting findByServerId(String serverId);

	List<ProducerServerAlarmSetting> findByPage(int offset, int limit);
	
	long count();
}
