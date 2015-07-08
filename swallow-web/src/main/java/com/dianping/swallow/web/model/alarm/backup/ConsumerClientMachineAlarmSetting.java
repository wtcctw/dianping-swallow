package com.dianping.swallow.web.model.alarm.backup;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientMachineAlarmSetting {

	private String ip;

	private ConsumerBaseAlarmSetting baseSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ConsumerBaseAlarmSetting getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSetting(ConsumerBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

	@Override
	public String toString() {
		return "ConsumerClientMachineAlarmSetting[ ip = " + ip + ", baseSetting = " + baseSetting + "]";
	}

}
