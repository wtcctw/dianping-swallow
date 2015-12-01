package com.dianping.swallow.common.internal.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.TopicPartition;
import com.dianping.swallow.common.message.MessageId;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午5:52:16
 */
public class DefaultMessageId implements MessageId{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private TopicPartition topicPartition;
	
	private Long messageId;
	
	public DefaultMessageId(TopicPartition topicPartition, Long messageId){
		
		this.topicPartition = topicPartition;
		this.messageId = messageId;
	}
	
	@Override
	public TopicPartition getTopicPartition() {
		return topicPartition;
	}

	@Override
	public Long getMessageId() {
		
		return messageId;
	}

}
