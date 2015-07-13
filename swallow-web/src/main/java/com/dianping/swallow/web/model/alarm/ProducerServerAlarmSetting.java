package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ProducerServerAlarmSetting  extends BaseAlarmSetting{
	
	private List<String> whiteList;
	
	private QPSAlarmSetting defaultAlarmSetting;
	
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
