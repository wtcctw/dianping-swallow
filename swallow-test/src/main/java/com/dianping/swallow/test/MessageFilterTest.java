package com.dianping.swallow.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

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


}
