package com.dianping.swallow.web.model.alarm;

import java.util.List;

import org.springframework.data.annotation.Id;

public class ConsumerServerAlarmSetting {
	
	@Id
	private String id;

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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
