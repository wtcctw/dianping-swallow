package com.dianping.swallow.web.model.event;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 上午11:13:13
 */
public enum EventType {
	/**
	 * 
	 */
	PRODUCER, 
	/**
	 * 
	 */
	CONSUMER;

	public boolean isProducerType() {
		return this == PRODUCER;
	}

	public boolean isConsumerType() {
		return this == CONSUMER;
	}
}
