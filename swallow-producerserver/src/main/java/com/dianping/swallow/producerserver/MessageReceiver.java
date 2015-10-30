package com.dianping.swallow.producerserver;

import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年10月30日 下午1:55:52
 */
public interface MessageReceiver {
	
	

	VALID_STATUS isTopicNameValid(String topicName);
	
	
	void receiveMessage(String topicName, String sourceDomain, SwallowMessage swallowMessage);

	
	
	public static enum VALID_STATUS{
		SUCCESS,
		TOPIC_NAME_INVALID,
		TOPIC_NAME_NOT_IN_WHITELIST
	}
}
