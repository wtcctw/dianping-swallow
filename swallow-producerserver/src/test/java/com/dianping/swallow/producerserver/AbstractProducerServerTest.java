package com.dianping.swallow.producerserver;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.mockito.Matchers;

import com.dianping.swallow.AbstractTest;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.common.server.monitor.collector.DefaultProducerCollector;
import com.dianping.swallow.producerserver.impl.DefaultMessageReceiver;

/**
 * @author mengwenchao
 *
 * 2015年10月30日 下午2:47:43
 */
public class AbstractProducerServerTest extends AbstractTest{
	
	
	protected MessageReceiver messageReceiver;
	
	@Before
	public void beforeAbstractProducerServerTest(){
		
		messageReceiver = createMessageReceiver();
		
	}
	
	private MessageReceiver createMessageReceiver(){
		
		DefaultMessageReceiver messageReceiver = new DefaultMessageReceiver();
		messageReceiver.setTopicWhiteList(createTopicWhiteList());
		messageReceiver.setMessageDao(createMessageDao());
		messageReceiver.setProducerCollector(new DefaultProducerCollector());
		
		return messageReceiver;
	}

	private TopicWhiteList createTopicWhiteList() {
		
        TopicWhiteList topicWhiteList = new TopicWhiteList();
        topicWhiteList.addTopic(topicName);
		return topicWhiteList;
	}

	private MessageDAO<?> createMessageDao() {
		
		return mock(MessageDAO.class);
	}

	protected void replaceExceptionDao() {
		
        MessageDAO<?> messageDao = mock(MessageDAO.class);
        doThrow(new RuntimeException()).when(messageDao).saveMessage(Matchers.anyString(), (SwallowMessage) Matchers.anyObject());
        
        ((DefaultMessageReceiver)messageReceiver).setMessageDao(messageDao);
	}



}
