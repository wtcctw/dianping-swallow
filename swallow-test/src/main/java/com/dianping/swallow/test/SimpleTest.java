package com.dianping.swallow.test;


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MessageDAOImpl;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoClient;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;

/**
 * 测试收发消息是否吻合
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:34:34
 */
public class SimpleTest extends AbstractTest{
	
	protected String topic = "swallow-test-integrated";
	protected String consumerId = "swallow-test-integrated-consumer";
	private   int  messageCount = 1000;
	private	  int concurrentCount = 50;

	@Before
	public void beforeSimpleTest(){
		
		
		MongoClient mc = new MongoClient("swallow.mongo.producerServerURI");
		MessageDAOImpl mdao = new MessageDAOImpl();
		mdao.setMongoClient(mc);
		
		mdao.cleanMessage(topic, consumerId);
		mdao.cleanMessage(topic, null);
	}
	
	@Test
	public void testDurableMessage() throws SendFailedException, RemoteServiceInitFailedException{
		
		addListener(topic, consumerId, concurrentCount);
		sendMessage(messageCount, topic);
		sleep(3000);
		Assert.assertEquals(messageCount, getConsumerMessageCount(topic, consumerId));
		
	}

	@Test
	public void testRestartMessage() throws SendFailedException, RemoteServiceInitFailedException{

		addListener(topic, consumerId, concurrentCount);
		sendMessage(messageCount, topic);
		
		sleep(5000);
		int currentConsumerCount = getConsumerMessageCount(topic, consumerId); 
		Assert.assertEquals(messageCount, currentConsumerCount);
		
		
		for(int i=0;i<5;i++){
			
			int concurrentSendMessageCount = getSendMessageCount(topic);
			
			//关闭consumer
			closeConsumer(topic, consumerId);
			sendMessage(messageCount, topic);
	
			//理论上应该不会收到消息
			currentConsumerCount = getConsumerMessageCount(topic, consumerId); 
			Assert.assertEquals(concurrentSendMessageCount, currentConsumerCount);
			
			startConsumer(topic, consumerId);
			sleep(5000);
		
			concurrentSendMessageCount = getSendMessageCount(topic);
			currentConsumerCount = getConsumerMessageCount(topic, consumerId); 
			Assert.assertEquals(concurrentSendMessageCount, currentConsumerCount);
		}
	}
}
