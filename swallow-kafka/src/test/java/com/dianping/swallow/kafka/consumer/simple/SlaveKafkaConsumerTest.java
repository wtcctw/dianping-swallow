package com.dianping.swallow.kafka.consumer.simple;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.kafka.KafkaMessage;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.consumer.AbstractKafkaConsumerTest;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午5:58:23
 */
public class SlaveKafkaConsumerTest extends AbstractKafkaConsumerTest{
	
	private SlaveKafkaConsumer slaveKafkaConsumer;
	
	@Before
	public void beforeSlaveKafkaConsumerTest(){
		slaveKafkaConsumer = new SlaveKafkaConsumer(getKafkaAddress(), "SlaveKafkaConsumerTest", 0, 5000, 1024*1024, 5000, 3, 8, 8, true, 1000);
		
	}
	
	@Test(expected = RuntimeException.class)
	public void testGetMaxMessageIdReplica1(){

		String topic = getTopicReplica1();
		TopicAndPartition tp = new TopicAndPartition(topic, 0);

		slaveKafkaConsumer.getMaxMessageId(tp);
	}
	
	
	
	@Test(expected = RuntimeException.class)
	public void testFromReplica1(){
		
		String topic = getTopicReplica1();
		TopicAndPartition tp = new TopicAndPartition(topic, 0);
		
		slaveKafkaConsumer.getMessageGreatThan(tp, 0L);
	}
	

	@Test
	public void testFromReplica2(){

		int sendMessageCount = 10;
		String topic = getTopicReplica2();
		TopicAndPartition tp = new TopicAndPartition(topic, 0);
		
		Long currentMessageId = slaveKafkaConsumer.getMaxMessageId(tp);
		
		sendMessage(topic, sendMessageCount);
		sleep(1000);
		
		Long maxIdAfterSend = slaveKafkaConsumer.getMaxMessageId(tp);
		
		Assert.assertEquals((Long)(currentMessageId + sendMessageCount), maxIdAfterSend);
		
		List<KafkaMessage> messages = slaveKafkaConsumer.getMessageGreatThan(tp, currentMessageId);

		for(KafkaMessage message : messages){
			System.out.println(message.getOffset());
		}
		Assert.assertEquals(sendMessageCount, messages.size());
	}

}
