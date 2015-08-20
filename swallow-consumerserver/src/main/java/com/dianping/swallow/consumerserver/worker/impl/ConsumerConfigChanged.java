package com.dianping.swallow.consumerserver.worker.impl;

import com.dianping.swallow.common.consumer.MessageFilter;

/**
 * @author mengwenchao
 *
 * 2015年8月17日 下午4:42:46
 */
public class ConsumerConfigChanged {
	
	
	private ConsumerConfigChangeType consumerConfigChangeType;
	
	private MessageFilter oldMessageFilter, newMessageFilter;

	public ConsumerConfigChanged(MessageFilter oldMessageFilter, MessageFilter newMessageFilter) {
		
		this.oldMessageFilter = oldMessageFilter;
		this.newMessageFilter = newMessageFilter;
		this.consumerConfigChangeType = ConsumerConfigChangeType.MESSAGE_FILTER;
	}

	
	public ConsumerConfigChangeType getConsumerConfigChangeType() {
		return consumerConfigChangeType;
	}

	public MessageFilter getOldMessageFilter() {
		return oldMessageFilter;
	}


	public MessageFilter getNewMessageFilter() {
		return newMessageFilter;
	}


	public static enum ConsumerConfigChangeType{
		
		MESSAGE_FILTER;
	}
}
