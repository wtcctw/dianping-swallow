package com.dianping.swallow.web.model.event;

public enum EventType {
	
	PRODUCER, CONSUMER;

	public boolean isProducerType() {
		return this == PRODUCER;
	}

	public boolean isConsumerType() {
		return this == CONSUMER;
	}
}
