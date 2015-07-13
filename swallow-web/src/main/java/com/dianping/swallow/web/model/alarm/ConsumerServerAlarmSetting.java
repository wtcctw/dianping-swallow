package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ConsumerServerAlarmSetting extends BaseAlarmSetting {

	private List<String> topicWhiteList;

	private QPSAlarmSetting senderAlarmSetting;

	private QPSAlarmSetting ackAlarmSetting;

	public QPSAlarmSetting getSenderAlarmSetting() {
		return senderAlarmSetting;
	}

	public void setSenderAlarmSetting(QPSAlarmSetting senderAlarmSetting) {
		this.senderAlarmSetting = senderAlarmSetting;
	}

	public QPSAlarmSetting getAckAlarmSetting() {
		return ackAlarmSetting;
	}

	public void setAckAlarmSetting(QPSAlarmSetting ackAlarmSetting) {
		this.ackAlarmSetting = ackAlarmSetting;
	}

	public List<String> getTopicWhiteList() {
		return topicWhiteList;
	}

	public void setTopicWhiteList(List<String> topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
