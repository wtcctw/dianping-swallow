package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class TopicAlarmSetting extends BaseAlarmSetting {
	
	private String topicName;

	private List<String> consumerIdWhiteList;

	private ProducerBaseAlarmSetting producerAlarmSetting;

	private ConsumerBaseAlarmSetting consumerAlarmSetting;

	public ProducerBaseAlarmSetting getProducerAlarmSetting() {
		return producerAlarmSetting;
	}

	public void setProducerAlarmSetting(ProducerBaseAlarmSetting producerAlarmSetting) {
		this.producerAlarmSetting = producerAlarmSetting;
	}

	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}

	public List<String> getConsumerIdWhiteList() {
		return consumerIdWhiteList;
	}

	public void setConsumerIdWhiteList(List<String> consumerIdWhiteList) {
		this.consumerIdWhiteList = consumerIdWhiteList;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

}
