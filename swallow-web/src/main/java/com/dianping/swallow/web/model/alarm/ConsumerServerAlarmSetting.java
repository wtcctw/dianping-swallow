package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ConsumerServerAlarmSetting extends BaseAlarmSetting {

	private String serverId;
	
	private List<String> topicWhiteList;

	private QPSAlarmSetting sendAlarmSetting;

	private QPSAlarmSetting ackAlarmSetting;

	public QPSAlarmSetting getSendAlarmSetting() {
		return sendAlarmSetting;
	}

	public void setSendAlarmSetting(QPSAlarmSetting sendAlarmSetting) {
		this.sendAlarmSetting = sendAlarmSetting;
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

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

}
