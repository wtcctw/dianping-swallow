package com.dianping.swallow.test;


import junit.framework.Assert;

import org.junit.Before;
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
public class GetFromSpecificTimeTest extends AbstractConsumerTest{
	
	private   int  messageCount = 1000;
	private	  int concurrentCount = 50;

	@Before
	public void beforeSimpleTest(){
		mdao.cleanMessage(topic, getConsumerId());
		mdao.cleanMessage(topic, null);
	}
	
	@Test
	public void testNoneDurableReceiveMessage() throws SendFailedException, RemoteServiceInitFailedException{
		
		Consumer consumer = addListener(topic, getConsumerId(), concurrentCount);
		sendMessage(messageCount, topic);
		sleep(3000);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
		
		
	}


	
	
}
