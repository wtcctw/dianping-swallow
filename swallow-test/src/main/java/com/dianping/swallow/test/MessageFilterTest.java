package com.dianping.swallow.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.consumer.Consumer;

/**
 * @author mengwenchao
 *
 * 2015年5月13日 下午6:03:03
 */
public class MessageFilterTest extends AbstractConsumerTest{

	protected String type = "filter";
	protected int messageCount = 1000;
	
	
	@Test
	public void filter() throws SendFailedException, RemoteServiceInitFailedException{
		
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		Consumer consumer = addListener(topic, getConsumerId(), filters);
		sendMessage(messageCount/2, topic);
		sendMessage(messageCount/2, topic, type);
		
		waitForListernToComplete(messageCount);
		Assert.assertEquals(messageCount/2, getConsumerMessageCount(consumer));
		
	}

	
	
	private volatile Set<String> types = new HashSet<String>();

	@Test
	@SuppressWarnings("unused")
	public void dynamicChange() throws SendFailedException, RemoteServiceInitFailedException{
		

		String type2 = "filter2";
		
		Set<String> filters = new HashSet<String>();
		filters.add(type);
		filters.add(type2);
		
		if(logger.isInfoEnabled()){
			logger.info(types.toString());
		}
		
		Consumer consumer = addListener(topic, getConsumerId(), filters);
		
		sendMessage(messageCount/2, topic, type);
		sendMessage(messageCount/2, topic, type2);
		
		waitForListernToComplete(messageCount);
		System.out.println(types + "," + types.size());
		Assert.assertEquals(2, types.size());

		types.clear();
		filters.remove(type);
		
		Consumer consumer2 = addListener(topic, getConsumerId(), filters);
		sendMessage(messageCount/2, topic, type);
		sendMessage(messageCount/2, topic, type2);
	
		waitForListernToComplete(messageCount);
		Assert.assertEquals(1, types.size());
	}
	
	@Override
	protected  synchronized void doOnMessage(Message msg) {
		
		types.add(msg.getType());
		if(logger.isDebugEnabled()){
			logger.debug(types + "," + msg.getType());
		}
	}

}
