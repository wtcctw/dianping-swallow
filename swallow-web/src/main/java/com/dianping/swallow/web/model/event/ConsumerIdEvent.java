package com.dianping.swallow.web.model.event;

public class ConsumerIdEvent extends TopicEvent{
	
	private String consumerId;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
}
