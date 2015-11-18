package com.dianping.swallow.common.internal.dao.impl;

import java.util.List;

import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月18日 下午3:46:24
 */
public class ReturnMessageWrapper {

	private List<SwallowMessage> messages;
	
	/**
	 * 消息大小，如果是包含有filter，则返回原生的获取消息大小
	 */
	private int rawMessageSize;
	
	private Long maxMessageId;

	public ReturnMessageWrapper(List<SwallowMessage> messages, int rawMessageSize, Long maxMessageId){
		this.messages = messages;
		this.rawMessageSize = rawMessageSize;
		this.maxMessageId = maxMessageId;
	}

	public List<SwallowMessage> getMessages() {
		return messages;
	}

	public int getRawMessageSize() {
		return rawMessageSize;
	}

	public Long getMaxMessageId() {
		return maxMessageId;
	}

}
