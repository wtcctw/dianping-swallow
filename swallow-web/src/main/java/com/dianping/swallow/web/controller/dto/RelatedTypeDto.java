package com.dianping.swallow.web.controller.dto;

/**
 * 
 * @author qiyin
 *
 *         2015年8月9日 下午4:26:44
 */
public enum RelatedTypeDto {
	EMPTY(""), IP("IP"), TOPIC("TOPIC"), CONSUMERID("CONSUMERID");

	private String desc;

	RelatedTypeDto(String desc) {
		this.setDesc(desc);
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public boolean isIp() {
		return this == IP;
	}

	public boolean isTopic() {
		return this == TOPIC;
	}

	public boolean isConsumerId() {
		return this == CONSUMERID;
	}
}
