package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerClientAlarmSetting {

	private ProducerClientBaseAlarmSetting baseSettings;

	private List<ProducerClientMachineAlarmSetting> machineSettings;

	public ProducerClientBaseAlarmSetting getBaseSettings() {
		return baseSettings;
	}

	public void setBaseSettings(ProducerClientBaseAlarmSetting baseSettings) {
		this.baseSettings = baseSettings;
	}

	public List<ProducerClientMachineAlarmSetting> getMachineSettings() {
		return machineSettings;
	}

	public void setMachineSettings(List<ProducerClientMachineAlarmSetting> machineSettings) {
		this.machineSettings = machineSettings;
	}

}
