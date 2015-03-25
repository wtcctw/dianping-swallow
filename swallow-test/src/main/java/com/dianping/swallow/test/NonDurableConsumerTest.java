package com.dianping.swallow.test;


import java.util.Date;

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
public class NonDurableConsumerTest extends AbstractConsumerTest{
	
	private   int  messageCount = 1000;
	private	  int concurrentCount = 50;

	
	@Test
	public void simpleSendMessage() throws SendFailedException, RemoteServiceInitFailedException{
		sendMessage(10, topic);
	}

	@Test
	public void testNoneDurableReceiveMessage() throws SendFailedException, RemoteServiceInitFailedException{
		
		Consumer consumer = addListener(topic, concurrentCount);
		Date date = new Date();
		sendMessage(messageCount, topic);
		sleep(3000);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
		addListener(topic, getConsumerId(), date, concurrentCount);
		sleep(3000);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
	}


}
