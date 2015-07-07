package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerServerAlarmSetting extends AbstractServerAlarmSetting{

	private ConsumerBaseAlarmSetting topicDefaultSetting;
	
	private List<ConsumerServerMachineAlarmSetting> machineAlarmSettings;

	public ConsumerBaseAlarmSetting getTopicDefaultSetting() {
		return topicDefaultSetting;
	}

	public void setTopicDefaultSetting(ConsumerBaseAlarmSetting topicDefaultSetting) {
		this.topicDefaultSetting = topicDefaultSetting;
	}
	
	@Override
	public String toString() {
		return "ConsumerServerAlarmSetting [topicDefaultSetting = " + topicDefaultSetting + " ]";
	}

	public List<ConsumerServerMachineAlarmSetting> getMachineAlarmSettings() {
		return machineAlarmSettings;
	}

	public void setMachineAlarmSettings(List<ConsumerServerMachineAlarmSetting> machineAlarmSettings) {
		this.machineAlarmSettings = machineAlarmSettings;
	}
	
}
