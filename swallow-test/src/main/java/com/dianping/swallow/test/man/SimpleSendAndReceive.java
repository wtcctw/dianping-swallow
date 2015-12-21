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
	public void simpleTest() throws SendFailedException, RemoteServiceInitFailedException{

		addListener(getTopic());

//		sendMessage(getTopic(), 10000, 5000);
		
	}
	
	@Test
	public void eternalSend() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{

		addListener(getTopic(), "notexist", 10, 0);

		for(int i = 0; i < 1;i++){
		}
//		addListener(getTopic());
		
//		sendMessage(getTopic(), 200, 0);

//		sendMessage(getTopic(), 200, 0);

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
		Consumer consumer = addListener(getTopic(), getConsumerId(), filters);
		@SuppressWarnings("unused")
		Consumer consumer1 = addListener(getTopic(), getConsumerId() + "-all", null);
		
		sendMessage(10, getTopic(), type);

		for(int i=0;i<600;i++){
			sendMessage(10, getTopic());
			TimeUnit.SECONDS.sleep(1);
		}
		
		sendMessage(10, getTopic(), type);
	
		
		sleep(1000000);
	}

	
	
	@Test
	public void testClose() throws InterruptedException{
		
		new Thread(new Runnable(){
			@Override
			public void run() {
				try {
					sendMessage(10000, getTopic());
				} catch (SendFailedException e) {
					e.printStackTrace();
				} catch (RemoteServiceInitFailedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	
		Consumer consumer = addListener(getTopic());
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

	
	@Override
	protected int consumerPrintCount() {
		return 1;
	}
}
