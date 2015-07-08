package com.dianping.swallow.web.model.alarm.backup;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientAlarmSetting {

	private List<String> machineWhiteList;

	private ProducerBaseAlarmSetting machineDefaultSetting;

	private List<ProducerClientMachineAlarmSetting> machineSettings;

	public List<ProducerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ProducerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}

	public List<String> getMachineWhiteList() {
		return machineWhiteList;
	}

	public void setMachineWhiteList(List<String> machineWhiteList) {
		this.machineWhiteList = machineWhiteList;
	}

	public ProducerBaseAlarmSetting getMachineDefaultSetting() {
		return machineDefaultSetting;
	}

	public void setMachineDefaultSetting(ProducerBaseAlarmSetting machineDefaultSetting) {
		this.machineDefaultSetting = machineDefaultSetting;
	}

	@Override
	public String toString() {
		return "ProducerClientAlarmSetting [ machineWhiteList = " + machineWhiteList + ", machineDefaultSetting = "
				+ machineDefaultSetting + ", machineSettings = " + machineSettings + "]";
	}

}
