package com.dianping.swallow.web.controller.dto;

public class ConsumerIdQueryDto extends BaseDto{
	
	private String consumerId;
	
	private String topic;
	
	private String consumerIp;
	
	private boolean inactive;
	
	public ConsumerIdQueryDto(){
		
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

	public String getConsumerIp() {
		return consumerIp;
	}

	public void setConsumerIp(String consumerIp) {
		this.consumerIp = consumerIp;
	}

	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}
	
}
