package com.dianping.swallow.web.model.resource;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:23:20
 */
@Document(collection = "CONSUMERID_RESOURCE")
public class ConsumerIdResource extends BaseResource{
	
	@Indexed
	private String consumerId;

	@Indexed
	private String topic;
	
	private boolean alarm;
	
	private List<String> consumerIp;
	
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

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
	
	public List<String> getConsumerIp() {
		return consumerIp;
	}

	public void setConsumerIp(List<String> consumerIp) {
		this.consumerIp = consumerIp;
	}

	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}

	@Override
	public String toString() {
		return "ConsumerIdResource [consumerId=" + consumerId + ", topic="
				+ topic + ", alarm=" + alarm + ", consumerIp=" + consumerIp
				+ ", consumerAlarmSetting=" + consumerAlarmSetting
				+ ", toString()=" + super.toString() + "]";
	}

}
