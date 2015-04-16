package com.dianping.swallow.test;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.junit.After;
import org.junit.Before;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.dao.impl.mongodb.DefaultMongoManager;
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
import com.dianping.swallow.test.AbstractTest;


/**
 * @author mengwenchao
 *
 * 2015年3月23日 下午4:27:18
 */
public abstract class AbstractSwallowTest extends AbstractTest{

	protected String topic = "swallow-test-integrated";
	

	protected ConcurrentHashMap<String, AtomicInteger> sendMessageCount = new ConcurrentHashMap<String, AtomicInteger>();

	protected ConcurrentHashMap<Consumer, AtomicInteger> getMessageCount = new ConcurrentHashMap<Consumer, AtomicInteger>();
	
	protected List<Consumer> consumers = new LinkedList<Consumer>();

	protected MessageDAOImpl mdao = new MessageDAOImpl();

	@Before
	public void beforeSwallowAbstractTest(){
		
		DefaultMongoManager mc = new DefaultMongoManager("swallow.mongo.producerServerURI");
		mdao = new MessageDAOImpl();
		mdao.setMongoManager(mc);

	}

	@After
	public void afterAbstratTest(){
		for(Consumer c : consumers){
			c.close();
		}
		sleep(100);
	}


	protected void sendMessage(int messageCount, String topic, int size) throws SendFailedException, RemoteServiceInitFailedException {
		sendMessage(messageCount, topic, 0, size);
	}
	
	protected void sendMessage(int messageCount, String topic) throws SendFailedException, RemoteServiceInitFailedException {
		
	}
	
	private AtomicInteger totalSend = new AtomicInteger();

	protected void sendMessage(int messageCount, String topic, int sleepInterval, int size) throws SendFailedException, RemoteServiceInitFailedException {
		
		String message = createMessage(size);
		
		AtomicInteger count = sendMessageCount.get(topic);
		if(count == null){
			count = new AtomicInteger();
			AtomicInteger old = sendMessageCount.putIfAbsent(topic, count); 
			if(old != null){
				count = old;
			}
		}
		
		Producer p = createProducer(topic);
        for (int i = 0; i < messageCount; i++) {
            String msg = System.currentTimeMillis() + "," + totalSend.incrementAndGet() + "," + message;
            p.sendMessage(msg);
            sleep(sleepInterval);
            count.incrementAndGet();
        }
	}


	protected Producer createProducer(String topic) throws RemoteServiceInitFailedException{
		
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);
        
        return p;

	}
	protected Producer createProducer(String topic, boolean zipped) throws RemoteServiceInitFailedException{
		
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        config.setZipped(zipped);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);
        
        return p;

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


	protected Consumer createConsumer(String topic, String consumerId){
		
		return createConsumer(topic, true, consumerId, 1, -1, 5);
	}

	protected Consumer createConsumer(String topic, String consumerId, int retryCount){
		
		return createConsumer(topic, true, consumerId, 1, -1, retryCount);
	}

	protected Consumer createConsumer(String topic, boolean durable, String consumerId, int concurrentCount, long startMessageId, int retryCount){

        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(concurrentCount);
        
        config.setDelayBaseOnBackoutMessageException(1);
        if(!durable){
        	config.setConsumerType(ConsumerType.NON_DURABLE);
        	if(consumerId != null){
        		throw new IllegalArgumentException("consumerId should be null, but " + consumerId);
        	}
        }
        config.setStartMessageId(startMessageId);
       
        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), consumerId, config);
        
        consumers.add(c);
        
        return c;
	}
	
	protected Consumer addListener(final String topic, boolean durable, final String consumerId, int concurrentCount, long startMessageId) {

		final Consumer c = createConsumer(topic, durable, consumerId, concurrentCount, startMessageId, 5);

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
            	
            	if(logger.isDebugEnabled()){
            		logger.debug("[onMessage]" + msg);
            	}
            	
            	int result = count.incrementAndGet();
            	if(result % 100 == 0 ){
            		if(logger.isInfoEnabled()){
            			logger.info("[onMessage]" + result);
            		}
            	}
            }
        });
        
        c.start();
        sleep(1000);
        return c;
	}

	protected void closeConsumer(Consumer consumer){
		consumer.close();
		sleep(100);
	}

	protected void startConsumer(Consumer consumer){
		consumer.start();
	}
	
	protected void restartConsumer(Consumer consumer){
		closeConsumer(consumer);
		startConsumer(consumer);
	}
	
	protected int getConsumerMessageCount(Consumer consumer){
		AtomicInteger count = getMessageCount.get(consumer);
		if(count == null){
			return 0;
		}
		return count.intValue();
	}

	protected void waitForListernToComplete(int messageCount) {
		
		if(messageCount <= 1000){
			sleep(4000);
			return;
		}
		sleep(8000);
	}



}
