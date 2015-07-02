package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientMachineAlarmSetting {

	private String ip;

	private ProducerClientBaseAlarmSetting baseSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ProducerClientBaseAlarmSetting getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSetting(ProducerClientBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

}
