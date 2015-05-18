package com.dianping.swallow.test;


import junit.framework.Assert;

import org.junit.Test;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;

/**
 * 测试收发消息是否吻合
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:34:34
 */
public class GetFromSpecificTimeTest extends AbstractConsumerTest{
	
	private   int  messageCount = 1000;
	private	  int  concurrentCount = 50;
	private   Long startMessageId;

	
	@Test
	public void testFromSpecificTime() throws SendFailedException, RemoteServiceInitFailedException{
		
		Long currentMessageId = getMaxMessageId(topic);
		
		startMessageId = currentMessageId;
		sendMessage(messageCount, topic);

		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
	}

	@Override
	protected void setConsumerConfig(ConsumerConfig config) {
		
		super.setConsumerConfig(config);
		if(startMessageId != null){
			if(logger.isInfoEnabled()){
				logger.info("[setConsumerConfig][set start message id]" + startMessageId);
			}
			config.setStartMessageId(startMessageId);
		}
	}
	
	
	@Test
	public void testRestartFromSpecificTime() throws SendFailedException, RemoteServiceInitFailedException{

		Long currentMessageId = getMaxMessageId(topic);
		
		sendMessage(messageCount, topic);

		startMessageId = currentMessageId;
		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
		for(int i=0;i<1;i++){
			
			closeConsumer(consumer);
			consumer = addListener(topic, getConsumerId(), concurrentCount);
			sendMessage(10, topic);
			waitForListernToComplete(messageCount);
			Assert.assertEquals(messageCount + 10, getConsumerMessageCount(consumer));
		}


	}
}
