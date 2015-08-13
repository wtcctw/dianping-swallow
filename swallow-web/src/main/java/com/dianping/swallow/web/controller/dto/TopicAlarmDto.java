package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月13日上午9:56:18
 */
public class TopicAlarmDto {
	
	private String topic;
	
	private boolean alarm;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	@Override
	public String toString() {
		return "TopicAlarmDto [topic=" + topic + ", alarm=" + alarm + "]";
	}
	
}
