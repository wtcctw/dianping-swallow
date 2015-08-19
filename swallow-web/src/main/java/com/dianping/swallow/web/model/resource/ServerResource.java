package com.dianping.swallow.web.model.resource;

import java.util.List;

import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午4:58:04
 */
public class ServerResource extends BaseResource{

	private String ip;
	
	private String hostname;
	
	private boolean alarm;
	
	private List<String> topicWhiteList;
	
	private QPSAlarmSetting sendAlarmSetting;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public List<String> getTopicWhiteList() {
		return topicWhiteList;
	}

	public void setTopicWhiteList(List<String> topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}
	
	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
	
	public QPSAlarmSetting getSendAlarmSetting() {
		return sendAlarmSetting;
	}

	public void setSendAlarmSetting(QPSAlarmSetting sendAlarmSetting) {
		this.sendAlarmSetting = sendAlarmSetting;
	}

	@Override
	public String toString() {
		return "ServerResource [ip=" + ip + ", hostname=" + hostname + ", alarm=" + alarm + ", topicWhiteList="
				+ topicWhiteList + ", sendAlarmSetting=" + sendAlarmSetting + ", toString()=" + super.toString() + "]";
	}

}
