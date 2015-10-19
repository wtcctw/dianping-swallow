package com.dianping.swallow.web.controller.dto;

import java.util.List;

import com.dianping.swallow.web.model.resource.IpInfo;


/**
 * @author mingdongli
 *
 * 2015年9月30日上午11:52:59
 */
public class TopicResourceDto extends BaseAlarmResourceDto{
	
	private String topic;
	
	private String administrator;
	
	private boolean producerAlarm;

	private boolean consumerAlarm;
	
	private List<IpInfo> producerIpInfos;

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

	public List<IpInfo> getProducerIpInfos() {
		return producerIpInfos;
	}

	public void setProducerIpInfos(List<IpInfo> producerIpInfos) {
		this.producerIpInfos = producerIpInfos;
	}

}
