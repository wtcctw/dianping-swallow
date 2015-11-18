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
	
	protected static final String BACKUP_TOPIC = "SWALLOW_BACKUP";

	private static final long serialVersionUID = 1L;

	public KafkaMessageDao(KafkaCluster cluster) {
		super(cluster);
	}
	
	@Override
	protected void doSaveMessage(final String topicName, String consumerId, SwallowMessage message) {

		String sendTopic = topicName;

		try {

			KafkaProducer<String, SwallowMessage>  producer = null;
			
			if(consumerId != null){
				 sendTopic = BACKUP_TOPIC;
			}
			
			producer = cluster.getProducer(sendTopic);

			ProducerRecord<String, SwallowMessage>  record = new ProducerRecord<String, SwallowMessage>(sendTopic, message);
			producer.send(record).get();
		} catch (Exception e) {
			throw new SwallowKafkaException("save message faild:" + sendTopic, e);
		}
	}

	@Override
	public void retransmitMessage(String topicName, SwallowMessage message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public SwallowMessage getMessage(String topicName, Long messageId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<SwallowMessage> getMessagesGreaterThan(String topicName, String consumerId, Long messageId, int size) {
		
		return null;
	}

	@Override
	public Long getMaxMessageId(String topicName, String consumerId) {
		
		return null;
	}

	@Override
	public SwallowMessage getMaxMessage(String topicName) {
		return null;
	}

	@Override
	public void cleanMessage(String topicName, String consumerId) {
		
	}

	@Override
	public int count(String topicName, String consumerId) {
		return 0;
	}

	@Override
	public long getAccumulation(String topicName, String consumerId) {
		return 0;
	}

	@Override
	public void cleanAck(String topicName, String consumerId, boolean isBackup) {
		
	}

	@Override
	public Long getAckMaxMessageId(String topicName, String consumerId, boolean isBackup) {

		return null;
	}

	@Override
	public void addAck(String topicName, String consumerId, Long messageId, String desc, boolean isBackup) {
		
	}


	@Override
	public Long getMessageEmptyAckId(String topicName) {
		return -1L;
	}


}
