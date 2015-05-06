package com.dianping.swallow.test;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.swallow.common.internal.heartbeat.DefaultHeartBeatSender;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @author mengwenchao
 *
 * 2015年5月5日 下午11:11:10
 */
public class HeartBeatTest extends AbstractConsumerTest{
	
	private int concurrentCount = 10;
	
	@Test
	public void testNoHeartBeat(){
		((ConsumerFactoryImpl)ConsumerFactoryImpl.getInstance()).setHeartBeatSender(new HeartBeatSenderOnce());
		addListener(topic, "id1", concurrentCount);
		sleep(3000000);
	}

	@Test
	public void testTwo(){
		
		addListener(topic, "id1", concurrentCount);
		sleep(3000000);
	}
	
	private class HeartBeatSenderOnce extends DefaultHeartBeatSender{

		@Override
		protected ScheduledFuture<?> doSchedule(ScheduledExecutorService scheduled, Runnable task) {
			
			 return scheduled.schedule(task , HEART_BEAT_INTERVAL, TimeUnit.SECONDS);
		}

	}
}
