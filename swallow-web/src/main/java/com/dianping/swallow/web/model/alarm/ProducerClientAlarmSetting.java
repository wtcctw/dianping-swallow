package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientAlarmSetting {

	private ProducerClientBaseAlarmSetting baseSetting;

	private List<ProducerClientMachineAlarmSetting> machineSettings;

	public ProducerClientBaseAlarmSetting getBaseSetting() {
		return baseSetting;
	}

	public void setBaseSettings(ProducerClientBaseAlarmSetting baseSetting) {
		this.baseSetting = baseSetting;
	}

	public List<ProducerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ProducerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}
	
	@Override
	public String toString() {
		return "ProducerClientAlarmSetting [baseSetting = " + baseSetting + ", machineSettings = " + machineSettings
				+ "]";
	}

}
