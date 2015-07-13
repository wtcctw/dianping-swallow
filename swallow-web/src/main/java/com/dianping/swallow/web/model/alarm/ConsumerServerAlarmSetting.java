package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ConsumerServerAlarmSetting extends BaseAlarmSetting {

	private List<String> whiteList;

	private QPSAlarmSetting senderAlarmSetting;

	private QPSAlarmSetting ackAlarmSetting;

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}

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

}
