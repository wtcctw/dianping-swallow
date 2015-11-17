package com.dianping.swallow.kafka.consumer;

import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;

import com.dianping.swallow.kafka.AbstractKafkaTest;
import com.dianping.swallow.kafka.TopicAndPartition;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午5:58:44
 */
public class AbstractKafkaConsumerTest extends AbstractKafkaTest{

	@Rule
	public TestName testName = new TestName();
	
	private String topicReplica1 = "topicReplica1";
	
	private String topicReplica2 = "topicReplica2";

	protected void sendMessage(TopicAndPartition tp, int count) {
		
		sendMessage(tp.getTopic(), count, randomString());
	}


	protected void sendMessage(String topicReplica, int count) {
		sendMessage(topicReplica, count, randomString());
	}

	
	protected void sendMessage(TopicAndPartition tp, int messageCount, String content) {
		sendMessage(tp.getTopic(), messageCount, content);
	}


	protected void sendMessage(String topic, int count, String content) {

		Properties props = new Properties();
		
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getKafkaAddress());
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
		props.put(ProducerConfig.ACKS_CONFIG, "-1");

		KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
		
		for(int i =0; i < count ;i++){

			try {
				producer.send(new ProducerRecord<String, String>(topic, content)).get();
			} catch (Exception e) {
				throw new RuntimeException("error sending message", e);
			}
			
		}
		
		
	}
	
	
	protected String randomString(){
		
		return UUID.randomUUID().toString();
	}

	private AtomicInteger topicCount = new AtomicInteger();
	
	public String getRandomTopic() {
		
		int count = topicCount.incrementAndGet();
		if((count & 1) == 0){
			return topicReplica1;
		}
		return topicReplica2;
	}
	
	
	public String getTopicReplica1() {
		return topicReplica1;
	}
	
	public String getTopicReplica2() {
		return topicReplica2;
	}

	public void sleep(long miliSeconds){
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}
