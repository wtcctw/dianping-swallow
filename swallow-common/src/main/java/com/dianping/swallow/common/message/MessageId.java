package com.dianping.swallow.common.message;

import com.dianping.swallow.common.internal.dao.TopicPartition;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午5:48:25
 */
public interface MessageId {
	
	TopicPartition getTopicPartition();
	
	Long getMessageId();

}
