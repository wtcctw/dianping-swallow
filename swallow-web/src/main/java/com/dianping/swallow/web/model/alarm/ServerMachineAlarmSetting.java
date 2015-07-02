package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ServerMachineAlarmSetting {
	
	private String ip;
	
	private QPSAlarmSetting qpsAlarmSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public QPSAlarmSetting getQpsAlarmSetting() {
		return qpsAlarmSetting;
	}

	public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
		this.qpsAlarmSetting = qpsAlarmSetting;
	}
	

}
