package com.dianping.swallow.test.man;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

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
			addListener(topic, "id" + i, 10, 0);
		}
//		addListener(topic);
		
//		sendMessage(topic, 200, 0);

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
	
	
}
