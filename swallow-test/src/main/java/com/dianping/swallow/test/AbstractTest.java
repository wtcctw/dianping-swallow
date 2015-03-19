package com.dianping.swallow.test;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoClient;
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
	
	protected String topic = "swallow-test-integrated";
	
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ConcurrentHashMap<String, AtomicInteger> sendMessageCount = new ConcurrentHashMap<String, AtomicInteger>();

	protected ConcurrentHashMap<Consumer, AtomicInteger> getMessageCount = new ConcurrentHashMap<Consumer, AtomicInteger>();
	
	protected List<Consumer> consumers = new LinkedList<Consumer>();

	protected MessageDAOImpl mdao = new MessageDAOImpl();

	@Rule
	public TestName  testName = new TestName();

	
	@Before
	public void beforeAbstractTest(){
		
		MongoClient mc = new MongoClient("swallow.mongo.producerServerURI");
		mdao = new MessageDAOImpl();
		mdao.setMongoClient(mc);

	}

	@After
	public void afterAbstratTest(){
		for(Consumer c : consumers){
			c.close();
		}
	}

	
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

	/**
	 * NON_DURABLE模式
	 * @param topic
	 * @param concurrentCount
	 * @return 
	 */
	protected Consumer addListener(final String topic, int concurrentCount) {
		
		return addListener(topic, false, null, concurrentCount, -1);
	}
	
	protected Consumer addListener(final String topic, final String consumerId, int concurrentCount) {
		
		return addListener(topic, true, consumerId, concurrentCount, -1);
	}

	protected Consumer addListener(String topic, String consumerId, Date date, int concurrentCount) {
		return addListener(topic, true, consumerId, concurrentCount, ConsumerConfig.fromDateToMessageId(date));
	}

	protected Consumer addListener(final String topic, boolean durable, final String consumerId, int concurrentCount, long startMessageId) {

        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(concurrentCount);
        
        if(!durable){
        	config.setConsumerType(ConsumerType.NON_DURABLE);
        	if(consumerId != null){
        		throw new IllegalArgumentException("consumerId should be null, but " + consumerId);
        	}
        }
        config.setStartMessageId(startMessageId);
       
        final Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId, config);
        c.setListener(new MessageListener() {
        	
        	AtomicInteger count;
        	{
            	count = getMessageCount.get(c);
            	if(count == null){
            		count = new AtomicInteger();
            		AtomicInteger old = getMessageCount.putIfAbsent(c, count);
            		if(old != null){
            			count = old;
            		}
            	}
        	}
            @Override
            public void onMessage(Message msg) {
            	int result = count.incrementAndGet();
            	if(result % 100 == 0 ){
            		System.out.println(result);
            	}
            }
        });
        
        consumers.add(c);
        c.start();
        sleep(100);
        return c;
	}

	protected void closeConsumer(Consumer consumer){
		consumer.close();
	}

	protected void startConsumer(Consumer consumer){
		consumer.start();
	}
	
	protected void restartConsumer(Consumer consumer){
		
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

	protected int getConsumerMessageCount(Consumer consumer){
		AtomicInteger count = getMessageCount.get(consumer);
		if(count == null){
			return 0;
		}
		return count.intValue();
	}

	
}
