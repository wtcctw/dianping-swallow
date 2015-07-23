package com.dianping.swallow.web.manager;

import com.dianping.swallow.web.model.alarm.AlarmType;

/**
 * 
 * @author qiyin
 *
 */
public interface AlarmManager {

	public void producerServerAlarm(String ip, AlarmType alarmType);

	public void producerServerStatisAlarm(String ip, long currentValue, long expectedValue, AlarmType alarmType);

	public void producerTopicStatisAlarm(String topic, long currentValue, long expectedValue, AlarmType alarmType);

	public void consumerServerAlarm(String masterIp, String slaveIp, AlarmType alarmType);

	public void consumerServerStatisAlarm(String ip, long currentValue, long expectedValue, AlarmType alarmType);

	public void consumerTopicStatisAlarm(String topic, long currentValue, long expectedValue, AlarmType alarmType);

	public void consumerIdStatisAlarm(String topic, String consumerId, long currentValue, long expectedValue,
			AlarmType alarmType);

}
