package com.dianping.swallow.web.model.alarm;

import java.util.Date;

import org.springframework.data.annotation.Id;

public class SwallowAlarmSetting {

	@Id
	private String id;

	private String produceWhiteList;

	private QPSAlarmSetting producerDefaultSetting;

	private String consumerWhiteList;

	private QPSAlarmSetting consumerDefaultSetting;

	private Date createTime;

	private Date updateTime;

	@Override
	public String toString() {
		return "SwallowAlarmSetting [id = " + id + ", produceWhiteList = " + produceWhiteList
				+ ", producerDefaultSetting = " + producerDefaultSetting + ", consumerWhiteList = " + consumerWhiteList
				+ ", consumerDefaultSetting = " + consumerDefaultSetting + ", createTime = " + createTime
				+ ", updateTime = " + updateTime + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getProduceWhiteList() {
		return produceWhiteList;
	}

	public void setProduceWhiteList(String produceWhiteList) {
		this.produceWhiteList = produceWhiteList;
	}

	public QPSAlarmSetting getProducerDefaultSetting() {
		return producerDefaultSetting;
	}

	public void setProducerDefaultSetting(QPSAlarmSetting producerDefaultSetting) {
		this.producerDefaultSetting = producerDefaultSetting;
	}

	public String getConsumerWhiteList() {
		return consumerWhiteList;
	}

	public void setConsumerWhiteList(String consumerWhiteList) {
		this.consumerWhiteList = consumerWhiteList;
	}

	public QPSAlarmSetting getConsumerDefaultSetting() {
		return consumerDefaultSetting;
	}

	public void setConsumerDefaultSetting(QPSAlarmSetting consumerDefaultSetting) {
		this.consumerDefaultSetting = consumerDefaultSetting;
	}

}
