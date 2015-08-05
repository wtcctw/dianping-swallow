package com.dianping.swallow.web.model.alarm;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:46:46
 */
public class ProducerServerAlarmSetting  extends BaseAlarmSetting{
	
	private String serverId;
	
	private List<String> topicWhiteList;
	
	private QPSAlarmSetting alarmSetting;
	
	public QPSAlarmSetting getAlarmSetting() {
		return alarmSetting;
	}

	public void setAlarmSetting(QPSAlarmSetting alarmSetting) {
		this.alarmSetting = alarmSetting;
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
