package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.util.LinkedList;
import java.util.List;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;

import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDao;
import com.dianping.swallow.common.internal.dao.impl.ReturnMessageWrapper;
import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.kafka.KafkaConsumer;
import com.dianping.swallow.kafka.KafkaMessage;
import com.dianping.swallow.kafka.TopicAndPartition;

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

		String sendTopic = getRealTopic(topicName, consumerId);

		try {

			KafkaProducer<String, SwallowMessage>  producer = null;
			
			if(consumerId != null){
				 message.putInternalProperty(InternalProperties.TOPIC, topicName);
				 message.putInternalProperty(InternalProperties.CONSUMERID, consumerId);
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
	public ReturnMessageWrapper getMessagesGreaterThan(final String topicName, String consumerId, Long messageId, int size) {

		//size ignore
		return getMessagesGreaterThan(topicName, consumerId, messageId);
	}

	private ReturnMessageWrapper getMessagesGreaterThan(String topicName, String consumerId, Long messageId) {
		
		String realTopic = getRealTopic(topicName, consumerId);
		
		KafkaConsumer kafkaConsumer = cluster.getConsumer(realTopic);
		Deserializer<SwallowMessage> deserializer = cluster.getDeserializer(realTopic);
		
		List<KafkaMessage> messages = kafkaConsumer.getMessageGreatThan(new TopicAndPartition(realTopic), messageId);

		int rawMessageSize = messages.size();
		Long maxMessageId = -1L;

		List<SwallowMessage> result = new LinkedList<SwallowMessage>();
		
		for(KafkaMessage kafkaMessage : messages){
			
			SwallowMessage message =  kafkaMessage.deserializer(deserializer);
			if(message.getMessageId() > maxMessageId){
				maxMessageId = message.getMessageId();
			}
			
			if(accept(message, topicName, consumerId)){
				result.add(message);
			}
		}
		
		return new ReturnMessageWrapper(result, rawMessageSize, maxMessageId);
	}

	private boolean accept(SwallowMessage message, String topicName, String consumerId) {
		
		if(consumerId == null){
			return true;
		}
		
		String backupTopic = message.getInternalProperty(InternalProperties.TOPIC);
		String backConsumerId = message.getInternalProperty(InternalProperties.CONSUMERID);
		
		if(backupTopic != null && backupTopic.equals(topicName)){
			if(backConsumerId != null && backConsumerId.equals(consumerId)){
				return true;
			}
		}
		return false;
	}

	private String getRealTopic(String topicName, String consumerId) {
		
		if(consumerId != null){
			return BACKUP_TOPIC;
		}
		return topicName;
	}

	@Override
	public Long getMaxMessageId(String topicName, String consumerId) {
		
		String realTopic = getRealTopic(topicName, consumerId);
		KafkaConsumer kafkaConsumer = cluster.getConsumer(realTopic);
		
		return kafkaConsumer.getMaxMessageId(new TopicAndPartition(topicName));
	}

	@Override
	public SwallowMessage getMaxMessage(String topicName) {
		
		Long maxId = getMaxMessageId(topicName);
		
		ReturnMessageWrapper wrapper = getMessagesGreaterThan(topicName, null, maxId - 1);
		
		List<SwallowMessage> messages = wrapper.getMessages();
		
		if(messages != null && messages.size() > 0){
			return messages.get(messages.size() - 1);
		}
		return null;
	}

	@Override
	public void cleanMessage(String topicName, String consumerId) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int count(String topicName) {
		
		KafkaConsumer kafkaConsumer = cluster.getConsumer(topicName);
		
		TopicAndPartition tp = new TopicAndPartition(topicName);
		Long maxId = kafkaConsumer.getMaxMessageId(tp); 
		Long minId = kafkaConsumer.getMinMessageId(tp);
		
		if(maxId == null || minId == null){
			return 0;
		}
		
		return (int) (maxId - minId + 1);
	}

	@Override
	public long getAccumulation(String topicName, String consumerId) {
		
		KafkaConsumer kafkaConsumer = cluster.getConsumer(topicName);
		
		TopicAndPartition tp = new TopicAndPartition(topicName);
		Long maxId = kafkaConsumer.getMaxMessageId(tp);
		Long currentAckId = kafkaConsumer.getAck(tp, consumerId);
		
		if(currentAckId == null){
			currentAckId = 0L;
			
		}
		
		return maxId - currentAckId;
	}

	@Override
	public void cleanAck(String topicName, String consumerId, boolean isBackup) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Long getAckMaxMessageId(String topicName, String consumerId, boolean isBackup) {

		TopicAndPartition tp = new TopicAndPartition(topicName);
		
		if(!isBackup){
			KafkaConsumer kafkaConsumer = cluster.getConsumer(topicName);
			return kafkaConsumer.getAck(tp, consumerId);
		}
		
		return cluster.getBackupAck(tp, consumerId);
	}

	@Override
	public void addAck(String topicName, String consumerId, Long messageId, String desc, boolean isBackup) {
		
		TopicAndPartition tp = new TopicAndPartition(topicName);
		if(!isBackup){
			KafkaConsumer kafkaConsumer = cluster.getConsumer(topicName);
			kafkaConsumer.saveAck(tp, consumerId, messageId);
			return;
		}
		
		cluster.saveBackupAck(tp, consumerId, messageId);
	}


	@Override
	public Long getMessageEmptyAckId(String topicName) {
		return -1L;
	}


}
