package com.dianping.swallow.web.controller.dto;

public class ConsumerIdQueryDto extends BaseDto{
	
	private String consumerId;
	
	private String topic;
	
	private String consumerIp;
	
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
	
}
