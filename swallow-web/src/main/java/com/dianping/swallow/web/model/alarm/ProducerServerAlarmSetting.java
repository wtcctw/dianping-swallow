package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ProducerServerAlarmSetting extends AbstractServerAlarmSetting {

	private ProducerBaseAlarmSetting topicDefaultSetting;
	
	private List<ProducerServerMachineAlarmSetting> machineAlarmSettings;
	

	public ProducerBaseAlarmSetting getTopicDefaultSetting() {
		return topicDefaultSetting;
	}

	public void setTopicDefaultSetting(ProducerBaseAlarmSetting topicDefaultSetting) {
		this.topicDefaultSetting = topicDefaultSetting;
	}
	
	public List<ProducerServerMachineAlarmSetting> getMachineAlarmSettings() {
		return machineAlarmSettings;
	}

	public void setMachineAlarmSettings(List<ProducerServerMachineAlarmSetting> machineAlarmSettings) {
		this.machineAlarmSettings = machineAlarmSettings;
	}

	@Override
	public String toString() {
		return "ProducerServerAlarmSetting [topicDefaultSetting = " + topicDefaultSetting + " ]";
	}

}
