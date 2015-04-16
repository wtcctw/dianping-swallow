package com.dianping.swallow.test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.producer.Producer;

/**
 * @author mengwenchao
 *
 * 2015年3月19日 下午6:05:34
 */
public class GzipDataTest extends AbstractConsumerTest {
	

	@Test
	public void testGzip() throws Throwable{
		
		String dataToSend = UUID.randomUUID().toString() + createMessage(1024*1024);
		final AtomicReference<String> data = new AtomicReference<String>();
		
		Consumer c = createConsumer(topic, getConsumerId());
		c.setListener(new MessageListener() {
			
			@Override
			public void onMessage(Message msg) throws BackoutMessageException {
				data.set(msg.getContent());
			}
		});
		c.start();

		Producer p = createProducer(topic, true);
		p.sendMessage(dataToSend);

		waitForListernToComplete(1);
		
		Assert.assertEquals(dataToSend, data.get());
	}
}
