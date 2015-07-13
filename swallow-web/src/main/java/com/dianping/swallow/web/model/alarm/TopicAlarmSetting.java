package com.dianping.swallow.web.model.alarm;

import java.util.List;

public class TopicAlarmSetting extends BaseAlarmSetting {

	private List<String> whiteList;

	private ProducerBaseAlarmSetting producerAlarmSetting;

	private ConsumerBaseAlarmSetting consumerAlarmSetting;

	public List<String> getWhiteList() {
		return whiteList;
	}

	public void setWhiteList(List<String> whiteList) {
		this.whiteList = whiteList;
	}

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

}
