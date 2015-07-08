package com.dianping.swallow.web.model.alarm.backup;

import java.util.List;

public class ConsumerIdAlarmSetting {

	private String consumerId;

	private List<String> machineWhiteList;

	private ConsumerBaseAlarmSetting machineDefalutSetting;

	private List<ConsumerClientMachineAlarmSetting> machineSettings;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public List<ConsumerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ConsumerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}

	public List<String> getMachineWhiteList() {
		return machineWhiteList;
	}

	public void setMachineWhiteList(List<String> machineWhiteList) {
		this.machineWhiteList = machineWhiteList;
	}

	public ConsumerBaseAlarmSetting getMachineDefalutSetting() {
		return machineDefalutSetting;
	}

	public void setMachineDefalutSetting(ConsumerBaseAlarmSetting machineDefalutSetting) {
		this.machineDefalutSetting = machineDefalutSetting;
	}

	@Override
	public String toString() {
		return "ConsumerIdAlarmSetting [consumerId = " + consumerId + ", machineWhiteList = " + machineWhiteList
				+ ", machineDefalutSetting = " + machineDefalutSetting + ", machineSettings=" + machineSettings + "]";
	}
}
