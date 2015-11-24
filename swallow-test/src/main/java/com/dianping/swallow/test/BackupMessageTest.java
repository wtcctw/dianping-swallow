package com.dianping.swallow.test;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;

/**
 * 
 * 此测试用例需要调整consumer端的参数：<br/>
 * 
 * waitAckExpiredSecond = 2
 * @author mengwenchao
 *
 * 2015年11月24日 下午2:02:38
 */
public class BackupMessageTest extends AbstractConsumerTest{
	
	private int waitAckExpiredSecond = 2;
	
	private CountDownLatch latch;
	
	private final int messageCount = 5;

	
	@Test
	public void testBackup() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{
		

		latch = new CountDownLatch(messageCount);
				
		Consumer consumer = addListener(topic, getConsumerId(), messageCount);
		
		sendMessage(messageCount, topic);

		latch.await();
		sleep(waitAckExpiredSecond * 1000);
		
		Assert.assertEquals(messageCount * 2 - 1, getConsumerMessageCount(consumer));
		
	}
	

	private NavigableMap<Long, Thread> threads = new ConcurrentSkipListMap<Long, Thread>();
	
	@SuppressWarnings("deprecation")
	@Override
	protected void doOnMessage(Message msg) {
		
		try{
			
			threads.put(msg.getMessageId(), Thread.currentThread());
			
			if(threads.size() == messageCount){
				threads.lastEntry().getValue().interrupt();
			}
			
			sleep((waitAckExpiredSecond * 3) * 1000);
		}finally{
			latch.countDown();
		}
	}
}
