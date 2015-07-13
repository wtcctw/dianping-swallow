package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class ProducerServerAlarmSetting  extends BaseAlarmSetting{
	
	private List<String> topicWhiteList;
	
	private QPSAlarmSetting defaultAlarmSetting;
	
	public QPSAlarmSetting getDefaultAlarmSetting() {
		return defaultAlarmSetting;
	}

	public void setDefaultAlarmSetting(QPSAlarmSetting defaultAlarmSetting) {
		this.defaultAlarmSetting = defaultAlarmSetting;
	}

	public List<String> getTopicWhiteList() {
		return topicWhiteList;
	}

	public void setTopicWhiteList(List<String> topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}
	
}
