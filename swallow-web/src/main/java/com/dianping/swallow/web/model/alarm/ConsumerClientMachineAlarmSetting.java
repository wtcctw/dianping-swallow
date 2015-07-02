package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientMachineAlarmSetting {

	private String ip;

	private ConsumerClientBaseAlarmSetting baseSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ConsumerClientBaseAlarmSetting getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSetting(ConsumerClientBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

	@Override
	public String toString() {
		return "ConsumerClientMachineAlarmSetting[ ip = " + ip + ", baseSetting = " + baseSetting + "]";
	}

}
