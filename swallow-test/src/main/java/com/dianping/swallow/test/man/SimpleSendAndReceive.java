package com.dianping.swallow.test.man;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.test.AbstractConsumerTest;

/**
 * @author mengwenchao
 *
 * 2015年5月26日 下午3:15:31
 */
public class SimpleSendAndReceive extends AbstractConsumerTest{
	
	
	@Test
	public void eternalSend() throws SendFailedException, RemoteServiceInitFailedException, InterruptedException{
		
		addListener(topic, "id1", 10, 10);
		addListener(topic, "id2", 10, 100);
		addListener(topic, "id3", 10, 1000);
		addListener(topic);
//		sendMessage(topic, 100000000, 40);

		TimeUnit.SECONDS.sleep(10000);
	}
	
	

	
	
}
