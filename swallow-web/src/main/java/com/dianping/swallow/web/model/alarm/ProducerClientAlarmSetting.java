package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientAlarmSetting {

	private String machineWhiteList;

	private ProducerClientBaseAlarmSetting machineDefaultSetting;

	private List<ProducerClientMachineAlarmSetting> machineSettings;

	public List<ProducerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ProducerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}

	public String getMachineWhiteList() {
		return machineWhiteList;
	}

	public void setMachineWhiteList(String machineWhiteList) {
		this.machineWhiteList = machineWhiteList;
	}

	public ProducerClientBaseAlarmSetting getMachineDefaultSetting() {
		return machineDefaultSetting;
	}

	public void setMachineDefaultSetting(ProducerClientBaseAlarmSetting machineDefaultSetting) {
		this.machineDefaultSetting = machineDefaultSetting;
	}

	@Override
	public String toString() {
		return "ProducerClientAlarmSetting [ machineWhiteList = " + machineWhiteList + ", machineDefaultSetting = "
				+ machineDefaultSetting + ", machineSettings = " + machineSettings + "]";
	}

}
