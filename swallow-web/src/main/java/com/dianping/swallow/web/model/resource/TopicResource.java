package com.dianping.swallow.web.model.resource;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午2:52:07
 */
@Document(collection = "TOPIC_RESOURCE")
public class TopicResource extends BaseResource{
	
	@Indexed
	private String name;
	
	private String prop;
	
	private boolean producerAlarm;

	private boolean consumerAlarm;
	
	private List<String> consumerIdWhiteList;

	private ProducerBaseAlarmSetting producerAlarmSetting;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public boolean isProducerAlarm() {
		return producerAlarm;
	}

	public void setProducerAlarm(boolean producerAlarm) {
		this.producerAlarm = producerAlarm;
	}

	public boolean isConsumerAlarm() {
		return consumerAlarm;
	}

	public void setConsumerAlarm(boolean consumerAlarm) {
		this.consumerAlarm = consumerAlarm;
	}

	public List<String> getConsumerIdWhiteList() {
		return consumerIdWhiteList;
	}

	public void setConsumerIdWhiteList(List<String> consumerIdWhiteList) {
		this.consumerIdWhiteList = consumerIdWhiteList;
	}

	public ProducerBaseAlarmSetting getProducerAlarmSetting() {
		return producerAlarmSetting;
	}

	public void setProducerAlarmSetting(ProducerBaseAlarmSetting producerAlarmSetting) {
		this.producerAlarmSetting = producerAlarmSetting;
	}

	@Override
	public String toString() {
		return "TopicResource [name=" + name + ", prop=" + prop + ", producerAlarm=" + producerAlarm
				+ ", consumerAlarm=" + consumerAlarm + ", consumerIdWhiteList=" + consumerIdWhiteList
				+ ", producerAlarmSetting=" + producerAlarmSetting + ", toString()=" + super.toString() + "]";
	}
	
}
