package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerServerMachineAlarmSetting {

	private String ip;

	private ProducerBaseAlarmSetting alarmSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "ServerMachineAlarmSetting [ip = " + ip + ", alarmSetting = " + alarmSetting + "]";
	}

}
