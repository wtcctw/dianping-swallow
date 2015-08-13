package com.dianping.swallow.web.controller.dto;

public class SendMessageDto {

	private String topic;

	private String content;

	private String type;

	private String delimitor;

	private String property;

	private String authentication;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "SendMessageDto [topic=" + topic + ", content=" + content + ", type=" + type + ", delimitor="
				+ delimitor + ", property=" + property + ", authentication=" + authentication + "]";
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDelimitor() {
		return delimitor;
	}

	public void setDelimitor(String delimitor) {
		this.delimitor = delimitor;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getAuthentication() {
		return authentication;
	}

	public void setAuthentication(String authentication) {
		this.authentication = authentication;
	}

}
