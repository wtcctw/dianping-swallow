package com.dianping.swallow.web.model.alarm;

import java.util.List;

import org.springframework.data.annotation.Id;

public class ProducerServerAlarmSetting {
	
	@Id
	private String id;
	
	private List<String> whiteList;
	
	private QPSAlarmSetting defaultAlarmSetting;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}

	public QPSAlarmSetting getDefaultAlarmSetting() {
		return defaultAlarmSetting;
	}

	public void setDefaultAlarmSetting(QPSAlarmSetting defaultAlarmSetting) {
		this.defaultAlarmSetting = defaultAlarmSetting;
	}
	
}
