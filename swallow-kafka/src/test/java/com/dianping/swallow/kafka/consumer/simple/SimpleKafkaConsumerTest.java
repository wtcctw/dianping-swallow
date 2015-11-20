package com.dianping.swallow.kafka.consumer.simple;

import java.util.List;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.kafka.KafkaMessage;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.AbstractKafkaConsumerTest;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午5:58:12
 */
public class SimpleKafkaConsumerTest extends AbstractKafkaConsumerTest{
	
	private SimpleKafkaConsumer simpleKafkaConsumer;

	private int testCount  = 20;

	
	@Before
	public void beforeSimpleKafkaConsumerTest(){
		
		simpleKafkaConsumer = new SimpleKafkaConsumer(getKafkaAddress(), "unittest", 0, 5000, 1 << 20, 5000, 3);
	}
	
	@Test
	public void testSaveAck(){
		
		System.out.println(simpleKafkaConsumer.getAck(new TopicAndPartition("SWALLOW_BACKUP", 0), "xxxxx"));
		
		TopicAndPartition tp = getTopicAndPartition();
		String group = testName.getMethodName();
		
		Long ack = 1L;
		
		for(int i =0;i<testCount;i++){
			
			simpleKafkaConsumer.saveAck(tp, group, ack);
			Long realAck = simpleKafkaConsumer.getAck(tp, group);
			
			Assert.assertEquals(ack, realAck);
			
			ack = ++realAck;
		}
	}

	@Test
	public void testGetMinMessageId(){
		
		TopicAndPartition tp = getTopicAndPartition();
		Long id = simpleKafkaConsumer.getMinMessageId(tp);
		if(logger.isInfoEnabled()){
			logger.info(Long.toString(id));
		}
	}

	
	@Test
	public void testGetMaxMessageId(){

		
		TopicAndPartition tp = getTopicAndPartition();
		int sendMessageCount = 10;
		
		for(int i=0;i<testCount;i++){
		
			Long currentOffset = simpleKafkaConsumer.getMaxMessageId(tp);
			
			sendMessage(tp, sendMessageCount);
			
			Long realOffset = simpleKafkaConsumer.getMaxMessageId(tp);
			
			Assert.assertEquals((Long)(currentOffset + sendMessageCount), realOffset);
			
		}
		
		
	}
	
	private TopicAndPartition getTopicAndPartition() {
		
		String topic = getTopicReplica2();
		TopicAndPartition tp = new TopicAndPartition(topic, 0);
		if(logger.isInfoEnabled()){
			logger.info("[getTopicAndPartition]" + tp);
		}
		return tp;
	}


	@Test
	public void testGetMessageGreatThan(){
		
		int messageCount = 100;
		TopicAndPartition tp = getTopicAndPartition();
		String message = randomString();
		
		Long currentOffset = simpleKafkaConsumer.getMaxMessageId(tp);
		
		sendMessage(tp, messageCount,  message);
		
		
		
		List<KafkaMessage> messages = simpleKafkaConsumer.getMessageGreatThan(tp, currentOffset);
		
		Assert.assertEquals(messageCount, messages.size());
		
		Long startOffset = currentOffset + 1;
		for(KafkaMessage km : messages){
			
			Assert.assertEquals(startOffset, km.getOffset());
			Assert.assertEquals(message, new String(km.getMessage()));
			
			startOffset++;
		}
		
	}
	
	@Test
	public void testMasterChange(){

		int messageCount = 100;
		TopicAndPartition tp = getTopicAndPartition();
		String message = randomString();
		
		Long currentOffset = simpleKafkaConsumer.getMaxMessageId(tp);
		
		sendMessage(tp, messageCount,  message);
		
		
		for(int i=0;i<1 << 30;i++){
			
			System.out.println("--------------begin fetch----------------");
			List<KafkaMessage> messages = simpleKafkaConsumer.getMessageGreatThan(tp, currentOffset);
			System.out.println(messages.size());
			
			sleep(1000);
		}
	}
	
	@Test
	public void testSendingMessage(){
		
		sendMessage(getTopicAndPartition(), 1);
	}

	@Override
	protected void afterSend(ProducerRecord<String, String> record) {
//		sleep(10);
	}

}
