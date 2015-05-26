package com.dianping.swallow.test;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.MessageRetryOnAllExceptionListener;

/**
 * @author mengwenchao
 *
 * 2015年3月19日 下午6:05:34
 */
public class RetryTest extends AbstractConsumerTest {
	
	int   retryCount = 5;
	
	private final int messageCount = 10;
	private AtomicInteger count = new AtomicInteger();
	private Consumer c;
	
	@Test
	public void testRetryOnBackout() throws Throwable{
		
		createRetryConsumerListener(true, retryCount);
		sendMessage(messageCount, topic);

		waitForListernToComplete(messageCount);
		Assert.assertEquals((retryCount + 1) * messageCount, count.get());
	}
	
	@Test
	public void testNoRetryOnBackout() throws Throwable{
		
		createRetryConsumerListener(false, retryCount);

		sendMessage(messageCount, topic);

		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount, count.get());
	}

	@Test
	public void testRetryOnAllException1() throws SendFailedException, RemoteServiceInitFailedException{
				
		createRetryConsumerListener(true, retryCount, true);
		
		sendMessage(messageCount, topic);

		waitForListernToComplete(messageCount);
		Assert.assertEquals((retryCount + 1) * messageCount, count.get());

	}

	@Test
	public void testRetryOnAllException2() throws SendFailedException, RemoteServiceInitFailedException{
				
		createRetryConsumerListener(false, retryCount, true);
		
		sendMessage(messageCount, topic);

		waitForListernToComplete(messageCount);
		Assert.assertEquals((retryCount + 1) * messageCount, count.get());

	}

	private void createRetryConsumerListener(boolean throwBackout, int retryCount){
		createRetryConsumerListener(throwBackout, retryCount, false);
	}

	
	private void createRetryConsumerListener(final boolean throwBackout, int retryCount, boolean isAllRetryListener) {
		c = createConsumer(topic, getConsumerId(), retryCount);
		if(!isAllRetryListener){
			c.setListener(new MessageListener() {
				
				@Override
				public void onMessage(Message msg) throws BackoutMessageException {
					count.incrementAndGet();
					if(throwBackout){
						throw new BackoutMessageException();
					}
					throw new IllegalArgumentException();
				}
			});
		}else{
			c.setListener(new MessageRetryOnAllExceptionListener() {
				@Override
				public void onMessage(Message msg) throws BackoutMessageException {
					count.incrementAndGet();
					if(throwBackout){
						throw new BackoutMessageException();
					}
					throw new IllegalArgumentException();
				}
			});
		}
		c.start();
		sleep(100);
	}


	@After
	public void afterRetryTest(){
		if(c != null){
			c.close();
//			sleep(100);
		}
	}
}
