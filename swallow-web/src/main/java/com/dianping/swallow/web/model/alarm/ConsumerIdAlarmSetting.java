package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:46:16
 */
public class ConsumerIdAlarmSetting extends BaseAlarmSetting {
	
	private String consumerId;
	
	private String topicName;
	
	private ConsumerBaseAlarmSetting consumerAlarmSetting;
	
	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
}
