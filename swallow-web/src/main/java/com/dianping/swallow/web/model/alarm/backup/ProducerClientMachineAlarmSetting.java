package com.dianping.swallow.web.model.alarm.backup;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientMachineAlarmSetting {

	private String ip;

	private ProducerBaseAlarmSetting baseSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public ProducerBaseAlarmSetting getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSetting(ProducerBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

	@Override
	public String toString() {
		return "ProducerClientMachineAlarmSetting [ip = " + ip + ",baseSetting=" + baseSetting + "]";
	}
}
