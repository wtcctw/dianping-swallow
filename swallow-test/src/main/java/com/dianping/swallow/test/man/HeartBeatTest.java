package com.dianping.swallow.test.man;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Test;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.test.AbstractConsumerTest;

/**
 * @author mengwenchao
 *
 * 2015年4月1日 上午10:36:43
 */
public class HeartBeatTest extends AbstractConsumerTest{
	
//	private final int messageCount = 3000;
	
	@Test
	public void testNormal() throws SendFailedException, RemoteServiceInitFailedException{
		
		@SuppressWarnings("unused")
		Consumer consumer = addListener(topic, 10);
		
//		sendMessage(messageCount, topic, 100);

//		sleep(messageCount);
//		Assert.assertEquals(messageCount, getConsumerMessageCount(consumer));
	}

	@After
	public void afterHeartBeatTest() throws InterruptedException{
		
		TimeUnit.SECONDS.sleep(6000);
	}
}
