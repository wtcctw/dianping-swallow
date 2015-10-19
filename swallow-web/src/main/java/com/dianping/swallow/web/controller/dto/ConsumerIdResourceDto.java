package com.dianping.swallow.web.controller.dto;

import java.util.List;

import com.dianping.swallow.web.model.resource.IpInfo;


/**
 * @author mingdongli
 *
 * 2015年7月15日下午2:56:03
 */
public class ConsumerIdResourceDto extends BaseAlarmResourceDto{
	
	private String consumerId;

	private String topic;
	
	private long ackpeak;
	
	private long ackvalley;
	
	private int ackfluctuation;
	
	private long ackfluctuationBase;

	private long ackdelay;
	
	private long accumulation;
	
	private List<IpInfo> consumerIpInfos;

	public List<IpInfo> getConsumerIpInfos() {
		return consumerIpInfos;
	}

	public void setConsumerIpInfos(List<IpInfo> consumerIpInfos) {
		this.consumerIpInfos = consumerIpInfos;
	}

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

	public long getAckpeak() {
		return ackpeak;
	}

	public void setAckpeak(long ackpeak) {
		this.ackpeak = ackpeak;
	}

	public long getAckvalley() {
		return ackvalley;
	}

	public void setAckvalley(long ackvalley) {
		this.ackvalley = ackvalley;
	}

	public int getAckfluctuation() {
		return ackfluctuation;
	}

	public void setAckfluctuation(int ackfluctuation) {
		this.ackfluctuation = ackfluctuation;
	}

	public long getAckfluctuationBase() {
		return ackfluctuationBase;
	}

	public void setAckfluctuationBase(long ackfluctuationBase) {
		this.ackfluctuationBase = ackfluctuationBase;
	}

	public long getAckdelay() {
		return ackdelay;
	}

	public void setAckdelay(long ackdelay) {
		this.ackdelay = ackdelay;
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}
	
}
