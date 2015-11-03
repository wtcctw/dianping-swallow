package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.util.List;

import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDao;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午3:52:25
 */
public class KafkaMessageDao extends AbstractMessageDao{

	@Override
	public void saveMessage(String topicName, String consumerId, SwallowMessage message) {
		
	}

	@Override
	public void retransmitMessage(String topicName, SwallowMessage message) {
		
	}

	@Override
	public SwallowMessage getMessage(String topicName, Long messageId) {
		return null;
	}

	@Override
	public List<SwallowMessage> getMessagesGreaterThan(String topicName, String consumerId, Long messageId, int size) {
		return null;
	}

	@Override
	public Long getMaxMessageId(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getMaxMessageId(String topicName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.dianping.swallow.common.internal.dao.MessageDAO#getMaxMessage(java.lang.String)
	 */
	@Override
	public SwallowMessage getMaxMessage(String topicName) {
		return null;
	}

	@Override
	public void cleanMessage(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int deleteMessage(String topicName, Long messageId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteMessage(String topicName, String consumerId, Long messageId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int count(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAccumulation(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		return 0;
	}

}
