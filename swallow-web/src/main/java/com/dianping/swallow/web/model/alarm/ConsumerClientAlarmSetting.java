package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientAlarmSetting {
	
	private String whiteList;

	private String consumerId;

	ConsumerClientBaseAlarmSetting baseSetting;

	private List<ConsumerClientMachineAlarmSetting> machineSettings;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public ConsumerClientBaseAlarmSetting getAlarmSetting() {
		return baseSetting;
	}

	public void setAlarmSetting(ConsumerClientBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

	public List<ConsumerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ConsumerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}

	@Override
	public String toString() {
		return "ConsumerClientAlarmSetting [whiteList = " + whiteList + ", consumerId = " + consumerId + ", baseSetting = " + baseSetting
				+ ", machineSettings = " + machineSettings + "]";
	}

	public String getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(String whiteList) {
		this.whiteList = whiteList;
	}
}
