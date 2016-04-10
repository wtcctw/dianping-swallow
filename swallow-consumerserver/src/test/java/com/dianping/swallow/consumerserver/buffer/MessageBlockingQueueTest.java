package com.dianping.swallow.consumerserver.buffer;


import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.buffer.impl.MessageBlockingQueue;

/**
 * @author mengwenchao
 *
 * 2015年2月12日 下午4:24:00
 */
public class MessageBlockingQueueTest extends AbstractTest{

	private MessageBlockingQueue queue =  
				new MessageBlockingQueue(
						new ConsumerInfo("UNIT_TEST", Destination.topic("UNIT_TEST_MessageBlockingQueueTest"), ConsumerType.DURABLE_AT_LEAST_ONCE),
				10, 100, Integer.MAX_VALUE, 0L, Executors.newCachedThreadPool());
	

	private AtomicInteger putCount = new AtomicInteger();
	private AtomicInteger retrieveCount = new AtomicInteger();
	
	@Test
	public void testEfficiency() throws InterruptedException{
		
		startFreQuencicyCounter();
		
		executors.execute(new Runnable(){

			@Override
			public void run() {
				while(true){
					queue.offer(new SwallowMessage());
					sleep(1);
					putCount.incrementAndGet();
				}
			}
		});
		
		executors.execute(new Runnable(){

			@Override
			public void run() {
				while(true){
					SwallowMessage message = queue.poll();
					if(message != null){
						retrieveCount.incrementAndGet();
					}
				}
			}
		});
		
		
	}

	private void startFreQuencicyCounter() {
		
		final int interval = 5;
		
		scheduledExecutors.scheduleAtFixedRate(new Runnable(){
			
			int lastPutCount = 0, lastRetrieveCount = 0;

			@Override
			public void run() {
				int put = putCount.get();
				int retrieve = retrieveCount.get();
				
				logger.info("[run][put vs retrieve]" + (put-lastPutCount)/interval + "," + (retrieve - lastRetrieveCount)/interval);
				
				lastPutCount = put;
				lastRetrieveCount = retrieve;
				
				
			}
		}, interval, interval, TimeUnit.SECONDS);
	}
	
	@After
	public void afterMessageBlockingQueueTest() throws InterruptedException{
		
		TimeUnit.SECONDS.sleep(1);
		
	}

}
