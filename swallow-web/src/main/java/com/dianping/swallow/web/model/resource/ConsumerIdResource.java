package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:23:20
 */
public class ConsumerIdResource extends BaseResource{
	
	@Indexed
	private String consumerId;

	@Indexed
	private String topic;
	
	private ConsumerBaseAlarmSetting consumerAlarmSetting;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}

	@Override
	public String toString() {
		return "ConsumerIdResource [consumerId=" + consumerId + ", topic=" + topic + ", consumerAlarmSetting="
				+ consumerAlarmSetting + ", toString()=" + super.toString() + "]";
	}

}
