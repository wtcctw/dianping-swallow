package com.dianping.swallow.web.controller.dto;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日上午10:41:45
 */
public class TopicAlarmSettingDto {
	
	private String topic;
	
	private String whitelist;
	
	private long producerpeak;
	
	private long producervalley;
	
	private int producerfluctuation;
	
	private long producerdelay;
	
	private long consumersendpeak;
	
	private long consumersendvalley;
	
	private int consumersendfluctuation;
	
	private long consumerackpeak;
	
	private long consumerackvalley;
	
	private int consumerackfluctuation;
	
	private long consumersenddelay;
	
	private long consumerackdelay;
	
	private long consumeraccumulation;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getWhitelist() {
		return whitelist;
	}

	public void setWhitelist(String whitelist) {
		this.whitelist = whitelist;
	}

	public long getProducerpeak() {
		return producerpeak;
	}

	public void setProducerpeak(long producerpeak) {
		this.producerpeak = producerpeak;
	}

	public long getProducervalley() {
		return producervalley;
	}

	public void setProducervalley(long producervalley) {
		this.producervalley = producervalley;
	}

	public int getProducerfluctuation() {
		return producerfluctuation;
	}

	public void setProducerfluctuation(int producerfluctuation) {
		this.producerfluctuation = producerfluctuation;
	}

	public long getProducerdelay() {
		return producerdelay;
	}

	public void setProducerdelay(long producerdelay) {
		this.producerdelay = producerdelay;
	}

	public long getConsumersendpeak() {
		return consumersendpeak;
	}

	public void setConsumersendpeak(long consumersendpeak) {
		this.consumersendpeak = consumersendpeak;
	}

	public long getConsumersendvalley() {
		return consumersendvalley;
	}

	public void setConsumersendvalley(long consumersendvalley) {
		this.consumersendvalley = consumersendvalley;
	}

	public int getConsumersendfluctuation() {
		return consumersendfluctuation;
	}

	public void setConsumersendfluctuation(int consumersendfluctuation) {
		this.consumersendfluctuation = consumersendfluctuation;
	}

	public long getConsumerackpeak() {
		return consumerackpeak;
	}

	public void setConsumerackpeak(long consumerackpeak) {
		this.consumerackpeak = consumerackpeak;
	}

	public long getConsumerackvalley() {
		return consumerackvalley;
	}

	public void setConsumerackvalley(long consumerackvalley) {
		this.consumerackvalley = consumerackvalley;
	}

	public int getConsumerackfluctuation() {
		return consumerackfluctuation;
	}

	public void setConsumerackfluctuation(int consumerackfluctuation) {
		this.consumerackfluctuation = consumerackfluctuation;
	}

	public long getConsumersenddelay() {
		return consumersenddelay;
	}

	public void setConsumersenddelay(long consumersenddelay) {
		this.consumersenddelay = consumersenddelay;
	}

	public long getConsumerackdelay() {
		return consumerackdelay;
	}

	public void setConsumerackdelay(long consumerackdelay) {
		this.consumerackdelay = consumerackdelay;
	}

	public long getConsumeraccumulation() {
		return consumeraccumulation;
	}

	public void setConsumeraccumulation(long consumeraccumulation) {
		this.consumeraccumulation = consumeraccumulation;
	}
	

}
