package com.dianping.swallow.test;


import org.junit.Assert;
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


	/**
	 * 此测试用例不可删除，准备dpsf可能耗费时间比较长，引发行为不一致
	 * @throws SendFailedException
	 * @throws RemoteServiceInitFailedException
	 */
	@Test
	public void testPrepareDpsf() throws SendFailedException, RemoteServiceInitFailedException {
		
		sendMessage(1, getTopic());
	}

	@Test
	public void testNoneDurableReceiveMessage() throws SendFailedException, RemoteServiceInitFailedException{
		
		Consumer consumer = addListener(getTopic(), concurrentCount);
		sendMessage(messageCount, getTopic());
		
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
				
	}

}
