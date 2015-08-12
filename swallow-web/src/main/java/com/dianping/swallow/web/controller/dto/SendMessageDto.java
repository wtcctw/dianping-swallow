package com.dianping.swallow.web.controller.dto;

public class SendMessageDto {

	private String mid;
	
	private String topic;
	
	private String authentication;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
	
	@Override
	public String toString() {
		return "SendMessageDto [mid=" + mid + ", topic=" + topic + "]";
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

}
