package com.dianping.swallow.web.model.resource;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:23:20
 */
@Document(collection = "CONSUMERID_RESOURCE")
@CompoundIndexes({
	@CompoundIndex(name = "IX_TOPIC_CONSUMERID", def = "{'topic': -1, 'consumerId': -1}")
	})
public class ConsumerIdResource extends BaseResource{
	
	private String consumerId;

	private String topic;
	
	private boolean alarm;
	
	private List<IpInfo> ipInfos;
	
	private String application;
	
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

	public ConsumerBaseAlarmSetting getConsumerAlarmSetting() {
		return consumerAlarmSetting;
	}

	public void setConsumerAlarmSetting(ConsumerBaseAlarmSetting consumerAlarmSetting) {
		this.consumerAlarmSetting = consumerAlarmSetting;
	}
	
	public String generateKey() {
		return topic + "&" + consumerId;
	}

	public List<IpInfo> getIpInfos() {
		return ipInfos == null ? new ArrayList<IpInfo>() : ipInfos;
	}

	public void setIpInfos(List<IpInfo> ipInfos) {
		this.ipInfos = ipInfos;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	@JsonIgnore
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(consumerId)) {
			return true;
		}
		return false;
	}
}
