package com.dianping.swallow.web.model.alarm.backup;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;

public class SwallowAlarmSetting {

	@Id
	private String id;

	private List<String> produceWhiteList;

	private ProducerBaseAlarmSetting producerDefaultSetting;

	private List<String> consumerWhiteList;

	private ConsumerBaseAlarmSetting consumerDefaultSetting;

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

	public List<String> getProduceWhiteList() {
		return produceWhiteList;
	}

	public void setProduceWhiteList(List<String> produceWhiteList) {
		this.produceWhiteList = produceWhiteList;
	}

	public ProducerBaseAlarmSetting getProducerDefaultSetting() {
		return producerDefaultSetting;
	}

	public void setProducerDefaultSetting(ProducerBaseAlarmSetting producerDefaultSetting) {
		this.producerDefaultSetting = producerDefaultSetting;
	}

	public List<String> getConsumerWhiteList() {
		return consumerWhiteList;
	}

	public void setConsumerWhiteList(List<String> consumerWhiteList) {
		this.consumerWhiteList = consumerWhiteList;
	}

	public ConsumerBaseAlarmSetting getConsumerDefaultSetting() {
		return consumerDefaultSetting;
	}

	public void setConsumerDefaultSetting(ConsumerBaseAlarmSetting consumerDefaultSetting) {
		this.consumerDefaultSetting = consumerDefaultSetting;
	}

}
