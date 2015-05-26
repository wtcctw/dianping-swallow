package com.dianping.swallow.consumerserver.buffer;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.AbstractConsumerServerSpringTest;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever.ReturnMessageWrapper;

/**
 * @author mengwenchao
 *
 * 2015年5月13日 下午2:21:50
 */
public class MongoDBMessageRetrieverTest extends AbstractConsumerServerSpringTest{
	
	private int insertCount = 100;
	
	@Before
	public void beforeMongoDBMessageRetrieverTest(){
		
		insertSwallowMessage(1);
	}

	@Test
	public void testRetriveMessage(){
		
		Long messageId = dao.getMaxMessageId(topicName);
		
		insertSwallowMessage(insertCount);
		Assert.assertNotNull(messageId);
		
		ReturnMessageWrapper wrapper = messageRetriever.retrieveMessage(topicName, messageId);
		
		Assert.assertEquals(insertCount, wrapper.getRawMessageSize());
		
	}
	
	@Test
	public void testRetriveMessageFilter(){

		Long messageId = dao.getMaxMessageId(topicName);
		int filterMessageCount = 50; 
		String filter = "filter1";
		
		for(int i=0;i<insertCount;i++){
			
			SwallowMessage message = createMessage();
			if(i < filterMessageCount){
				message.setType(filter);
				dao.saveMessage(topicName, message);
			}else{
				dao.saveMessage(topicName, message);
			}
		}
		
		Set<String> hashFilter = new HashSet<String>();
		hashFilter.add(filter);
		
		MessageFilter messageFilter = MessageFilter.createInSetMessageFilter(hashFilter);
		ReturnMessageWrapper wrapper = messageRetriever.retrieveMessage(topicName, null, messageId, messageFilter);
		Assert.assertEquals(insertCount, wrapper.getRawMessageSize());
		Assert.assertEquals(filterMessageCount, wrapper.getMessages().size());

	
		hashFilter.add("newFilter");
		messageFilter = MessageFilter.createInSetMessageFilter(hashFilter);
		wrapper = messageRetriever.retrieveMessage(topicName, null, messageId, messageFilter);
		Assert.assertEquals(insertCount, wrapper.getRawMessageSize());
		Assert.assertEquals(filterMessageCount, wrapper.getMessages().size());

		hashFilter.clear();
		messageFilter = MessageFilter.createInSetMessageFilter(hashFilter);
		wrapper = messageRetriever.retrieveMessage(topicName, null, messageId, messageFilter);
		Assert.assertEquals(insertCount, wrapper.getRawMessageSize());
		Assert.assertEquals(insertCount, wrapper.getMessages().size());
		
		hashFilter.add("nonexist");
		messageFilter = MessageFilter.createInSetMessageFilter(hashFilter);
		wrapper = messageRetriever.retrieveMessage(topicName, null, messageId, messageFilter);
		Assert.assertEquals(insertCount, wrapper.getRawMessageSize());
		Assert.assertEquals(0, wrapper.getMessages().size());


	}

}
