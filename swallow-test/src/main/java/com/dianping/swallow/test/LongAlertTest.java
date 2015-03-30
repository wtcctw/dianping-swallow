package com.dianping.swallow.test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.consumer.internal.ConsumerImpl;
import com.dianping.swallow.consumer.internal.task.TaskChecker;

/**
 * 运行时间过长，产生告警
 * @author mengwenchao
 *
 * 2015年3月19日 下午6:05:34
 */
public class LongAlertTest extends AbstractConsumerTest {
	
	private AtomicInteger count = new AtomicInteger();
	private Consumer c;
	
	@Test
	public void testAlertOnLongTask() throws Throwable{

		ConsumerConfig config = new ConsumerConfig();
		config.setLongTaskAlertTime(1000);
		
        c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), getConsumerId(), config);
        TaskChecker taskChecker =  ((ConsumerImpl)c).getTaskChecker();
		
		c.setListener(new MessageListener() {
			
			@Override
			public void onMessage(Message msg) throws BackoutMessageException {
				count.incrementAndGet();
				try {
					TimeUnit.SECONDS.sleep(10);
				} catch (InterruptedException e) {
				}
			}
		});
		
		c.start();
		
		sendMessage(1, topic);

		TimeUnit.SECONDS.sleep(5);
		
		Assert.assertEquals(1, count.get());
		c.close();
		int alertCount = taskChecker.getAlertCount();
		Assert.assertTrue(alertCount > 0);
		
		TimeUnit.SECONDS.sleep(5);
		int nowCount = taskChecker.getAlertCount();
		Assert.assertTrue(nowCount == alertCount || nowCount == (alertCount + 1));
	}
	

	@After
	public void afterRetryTest(){
		if(c != null){
			c.close();
		}
	}
}
