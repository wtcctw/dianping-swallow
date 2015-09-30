package com.dianping.swallow.web.model.resource;

import java.util.List;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午2:52:07
 */
@Document(collection = "TOPIC_RESOURCE")
public class TopicResource extends BaseResource {

	@Indexed(name = "IX_TOPIC", direction = IndexDirection.ASCENDING)
	private String topic;

	private String administrator;

	private boolean producerAlarm;

	private boolean consumerAlarm;

	private List<IpInfo> producerIpInfos;
	
	private List<String> producerApplications;

	private ProducerBaseAlarmSetting producerAlarmSetting;

	public List<IpInfo> getProducerIpInfos() {
		return producerIpInfos;
	}

	public void setProducerIpInfos(List<IpInfo> producerIpInfos) {
		this.producerIpInfos = producerIpInfos;
	}

	public List<String> getProducerApplications() {
		return producerApplications;
	}

	public void setProducerApplications(List<String> producerApplications) {
		this.producerApplications = producerApplications;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getAdministrator() {
		return administrator;
	}

	public void setAdministrator(String administrator) {
		this.administrator = administrator;
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

	public ProducerBaseAlarmSetting getProducerAlarmSetting() {
		return producerAlarmSetting;
	}

	public void setProducerAlarmSetting(ProducerBaseAlarmSetting producerAlarmSetting) {
		this.producerAlarmSetting = producerAlarmSetting;
	}

	@Override
	public String toString() {
		return "TopicResource [topic=" + topic + ", administrator=" + administrator + ", producerAlarm="
				+ producerAlarm + ", consumerAlarm=" + consumerAlarm + ", producerIpInfos=" + producerIpInfos
				+ ", producerApplications=" + producerApplications + ", producerAlarmSetting=" + producerAlarmSetting
				+ ", toString()=" + super.toString() + "]";
	}

	@JsonIgnore
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(topic)) {
			return true;
		}
		return false;
	}

}
