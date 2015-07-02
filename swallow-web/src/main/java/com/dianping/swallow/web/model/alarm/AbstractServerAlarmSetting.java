package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public abstract class AbstractServerAlarmSetting {
	
	private QPSAlarmSetting qpsAlarmSetting;

	private List<ServerMachineAlarmSetting> machineAlarmSettings;

	public QPSAlarmSetting getQpsAlarmSetting() {
		return qpsAlarmSetting;
	}

	public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
		this.qpsAlarmSetting = qpsAlarmSetting;
	}

	public List<ServerMachineAlarmSetting> getMachineAlarmSettings() {
		return machineAlarmSettings;
	}

	public void setMachineAlarmSettings(List<ServerMachineAlarmSetting> machineAlarmSettings) {
		this.machineAlarmSettings = machineAlarmSettings;
	}
}
