package com.dianping.swallow.test;


import junit.framework.Assert;

import org.junit.Test;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;

/**
 * 测试收发消息是否吻合
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:34:34
 */
public class DurableConsumerTest extends AbstractConsumerTest{
	
	private   int  messageCount = 1000;
	private	  int concurrentCount = 50;


	@Test
	public void testEternalSendAndReceive() throws SendFailedException, RemoteServiceInitFailedException{

		addListener(topic, getConsumerId(), concurrentCount, 100);
		addListener(topic, "id2", concurrentCount, 200);
		sendMessage(Integer.MAX_VALUE, topic, 1000);
		sleep(10000*1000);
	}
	
	@Test
	public void testDurableMessage() throws SendFailedException, RemoteServiceInitFailedException{
		
		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		sendMessage(messageCount, topic);
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
	}

	@Test
	public void testDurationClose() throws SendFailedException, RemoteServiceInitFailedException{
		
		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		closeConsumer(consumer);
		sendMessage(messageCount, topic);
		
		startConsumer(consumer);
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
	}

	@Test
	/**
	 * 测试consumer重启，能否收到消息 
	 * @throws SendFailedException
	 * @throws RemoteServiceInitFailedException
	 */
	public void testRestartMessage() throws SendFailedException, RemoteServiceInitFailedException{

		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		sendMessage(messageCount, topic);
		
		waitForListernToComplete(messageCount);
		int currentConsumerCount = getConsumerMessageCount(consumer); 
		Assert.assertEquals(messageCount, currentConsumerCount);
		
		
		for(int i=0;i<5;i++){
			
			int concurrentSendMessageCount = getSendMessageCount(topic);
			
			//关闭consumer
			closeConsumer(consumer);
			sendMessage(messageCount, topic);
	
			//理论上应该不会收到消息
			currentConsumerCount = getConsumerMessageCount(consumer); 
			Assert.assertEquals(concurrentSendMessageCount, currentConsumerCount);
			
			startConsumer(consumer);
			waitForListernToComplete(messageCount);
		
			concurrentSendMessageCount = getSendMessageCount(topic);
			currentConsumerCount = getConsumerMessageCount(consumer); 
			Assert.assertEquals(concurrentSendMessageCount, currentConsumerCount);
		}
	}
}
