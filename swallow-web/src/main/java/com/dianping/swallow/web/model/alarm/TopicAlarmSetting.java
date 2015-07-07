package com.dianping.swallow.web.model.alarm;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author qiyin
 *
 */
public class TopicAlarmSetting {

	@Id
	private String id;

	private String topicName;

	private ProducerClientAlarmSetting producerSetting;

	private ConsumerClientAlarmSetting consumerSetting;

	private Date createTime;

	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public ProducerClientAlarmSetting getProducerSetting() {
		return producerSetting;
	}

	public void setProducerSetting(ProducerClientAlarmSetting producerSetting) {
		this.producerSetting = producerSetting;
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

	public ConsumerClientAlarmSetting getConsumerSetting() {
		return consumerSetting;
	}

	public void setConsumerSetting(ConsumerClientAlarmSetting consumerSetting) {
		this.consumerSetting = consumerSetting;
	}
	
	@Override
	public String toString() {
		return "TopicAlarmSetting [ id = " + id + ",topicName = " + topicName + ", producerSetting = "
				+ producerSetting + ", consumerSetting = " + consumerSetting + ", createTime = " + createTime
				+ ", updateTime = " + updateTime + "]";
	}
}
