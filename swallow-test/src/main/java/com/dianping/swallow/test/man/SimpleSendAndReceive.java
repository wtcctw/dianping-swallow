package com.dianping.swallow.test.man;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.test.AbstractConsumerTest;

/**
 * @author mengwenchao
 *
 * 2015年5月26日 下午3:15:31
 */
public class SimpleSendAndReceive extends AbstractConsumerTest{
	
	
	@Test
	public void eternalSend() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{

		for(int i = 0; i < 1;i++){
//			addListener(topic, "id" + i, 10, 0);
		}
//		addListener(topic);
		
//		sendMessage(topic, 200, 0);

		sendMessage(topic, 200, 0);

		TimeUnit.SECONDS.sleep(10000);
	}
	
	
	@Override
	protected boolean isCleanData() {
		
		return false;
	}

	@Test
	public void testFilter() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{

		String type = "type";
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		@SuppressWarnings("unused")
		Consumer consumer = addListener(topic, getConsumerId(), filters);
		
		sendMessage(10, topic, type);

		for(int i=0;i<600;i++){
			sendMessage(10, topic);
			TimeUnit.SECONDS.sleep(1);
		}
		
		sendMessage(10, topic, type);
	
		
		sleep(1000000);
	}

	
	
	@Test
	public void testClose() throws InterruptedException{
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					sendMessage(10000, topic);
				} catch (SendFailedException e) {
					e.printStackTrace();
				} catch (RemoteServiceInitFailedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	
		Consumer consumer = addListener(topic);
		TimeUnit.SECONDS.sleep(5);
		
		System.out.println("begin close ");
		consumer.close();
		TimeUnit.SECONDS.sleep(50);
		
	}
	
	
	private AtomicInteger count = new AtomicInteger();
	@Override
	protected void doOnMessage(Message msg) {
		
		int total = count.incrementAndGet();
		if(total % 10 == 0){
			System.out.println(total);
		}
	}
	
}
