package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDao;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午3:52:25
 */
public class KafkaMessageDao extends AbstractMessageDao<KafkaCluster>{

	private static final long serialVersionUID = 1L;

	public KafkaMessageDao(KafkaCluster cluster) {
		super(cluster);
	}
	
	
	@Override
	protected void doSaveMessage(String topicName, String consumerId, SwallowMessage message) {
		
		if(consumerId != null){
			throw new IllegalArgumentException("consumerId != null, currently not supported!!");
		}
		
		try {
			KafkaProducer<String, SwallowMessage>  producer = cluster.getProducer(topicName);
			ProducerRecord<String, SwallowMessage>  record = new ProducerRecord<String, SwallowMessage>(topicName, message);
			producer.send(record).get();
		} catch (Exception e) {
			throw new SwallowKafkaException("save message faild:" + topicName, e);
		}
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
	public int count(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getAccumulation(String topicName, String consumerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void cleanAck(String topicName, String consumerId, boolean isBackup) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Long getAckMaxMessageId(String topicName, String consumerId, boolean isBackup) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAck(String topicName, String consumerId, Long messageId, String desc, boolean isBackup) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Long getMessageEmptyAckId(String topicName) {
		return -1L;
	}


}
