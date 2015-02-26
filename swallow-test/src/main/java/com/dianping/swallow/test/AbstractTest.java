package com.dianping.swallow.test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:29:16
 */
public class AbstractTest {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ConcurrentHashMap<String, AtomicInteger> sendMessageCount = new ConcurrentHashMap<String, AtomicInteger>();

	protected ConcurrentHashMap<ConsumerKey, AtomicInteger> getMessageCount = new ConcurrentHashMap<AbstractTest.ConsumerKey, AtomicInteger>();
	
	protected ConcurrentHashMap<ConsumerKey, Consumer> consumers = new ConcurrentHashMap<AbstractTest.ConsumerKey, Consumer>();

	protected void sendMessage(int messageCount, String topic) throws SendFailedException, RemoteServiceInitFailedException {
		
		AtomicInteger count = sendMessageCount.get(topic);
		if(count == null){
			count = new AtomicInteger();
			AtomicInteger old = sendMessageCount.putIfAbsent(topic, count); 
			if(old != null){
				count = old;
			}
		}
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);
        for (int i = 0; i < messageCount; i++) {
            String msg = i + "," + System.currentTimeMillis();
            p.sendMessage(msg);
            count.incrementAndGet();
        }
	}
	
	protected int getSendMessageCount(String topic){
		AtomicInteger count = sendMessageCount.get(topic);
		if(count == null){
			return 0;
		}
		return count.intValue();
	}

	protected void addListener(final String topic, final String consumerId, int concurrentCount) {

        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(concurrentCount);
    	
        final ConsumerKey key = new ConsumerKey(topic, consumerId);
    	
        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId, config);
        c.setListener(new MessageListener() {
        	
        	AtomicInteger count;
        	{
            	count = getMessageCount.get(key);
            	if(count == null){
            		count = new AtomicInteger();
            		AtomicInteger old = getMessageCount.putIfAbsent(key, count);
            		if(old != null){
            			count = old;
            		}
            	}
        	}
            @Override
            public void onMessage(Message msg) {
            	int result = count.incrementAndGet();
            	if(result % 50 == 0 ){
            		System.out.println(result);
            	}
            }
        });
        
        consumers.put(key, c);
        c.start();
        sleep(100);
	}

	protected void closeConsumer(String topic, String consumerId){
		
		Consumer consumer = consumers.get(new ConsumerKey(topic, consumerId));
		consumer.close();
	}

	protected void startConsumer(String topic, String consumerId){
		
		Consumer consumer = consumers.get(new ConsumerKey(topic, consumerId));
		consumer.start();
	}

	
	protected void restartConsumer(String topic, String consumerId){
		
		Consumer consumer = consumers.get(new ConsumerKey(topic, consumerId));
		consumer.close();
		consumer.start();
		
	}
	
	protected void sleep(int miliSeconds) {
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	protected int getConsumerMessageCount(String topic, String consumerId){
		AtomicInteger count = getMessageCount.get(new ConsumerKey(topic, consumerId));
		if(count == null){
			return 0;
		}
		return count.intValue();
	}

	
	class ConsumerKey{
		
		private String topic;
		private String consumerId;

		public ConsumerKey(String topic, String consumerId){
			this.topic = topic;
			this.consumerId = consumerId;
		}
		
		public String getTopic() {
			return topic;
		}
		public void setTopic(String topic) {
			this.topic = topic;
		}
		public String getConsumerId() {	
			return consumerId;
		}
		public void setConsumerId(String consumerId) {
			this.consumerId = consumerId;
		}
		
		@Override
		public int hashCode() {
			return new String(topic + consumerId).hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof ConsumerKey)){
				return false;
			}
			ConsumerKey comare = (ConsumerKey)obj;
			return topic.equals(comare.topic) && consumerId.equals(comare.consumerId);
		}
		
		
	}
}
