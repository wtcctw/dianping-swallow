package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ConsumerIdAlarmSetting {

	private String consumerId;

	private String machineWhiteList;

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

	@Override
	public String toString() {
		return "ConsumerIdAlarmSetting [consumerId = " + consumerId + ", machineWhiteList = " + machineWhiteList
				+ ", machineDefalutSetting = " + machineDefalutSetting + ", machineSettings=" + machineSettings + "]";
	}
}
