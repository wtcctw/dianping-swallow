package com.dianping.swallow.web.model.alarm;

public class ConsumerServerMachineAlarmSetting {
	
	private String ip;

	private ConsumerBaseAlarmSetting alarmSetting;

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
