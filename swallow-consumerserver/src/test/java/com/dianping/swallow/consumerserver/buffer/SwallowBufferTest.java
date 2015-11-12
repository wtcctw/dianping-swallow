package com.dianping.swallow.consumerserver.buffer;


import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.AbstractConsumerServerSpringTest;

public class SwallowBufferTest extends AbstractConsumerServerSpringTest {
	
    protected static final String TYPE       = "feed";

    protected SwallowBuffer         swallowBuffer;
    
    private Long                  tailMessageId;

    @Before
    public void setUp() throws Exception {
    	
    	
    	swallowBuffer = getBean(SwallowBuffer.class);
    	
        //插入1条消息
        SwallowMessage firstMsg = createMessage();
        firstMsg.setContent("content1");
        messageDao.saveMessage(topicName, firstMsg);
        //初始化tailMessageId
        tailMessageId = messageDao.getMaxMessageId(topicName);
        //添加9条Message
        int i = 2;
        while (i <= 10) {
            //插入消息
            SwallowMessage msg = createMessage();
            msg.setContent("content" + i++);
            messageDao.saveMessage(topicName, msg);
        }
    }

    @Test
    public void testCreateMessageQueue2() throws InterruptedException {
    	
        Set<String> messageTypeSet = new HashSet<String>();
        messageTypeSet.add(TYPE);
        ConsumerInfo consumerInfo = new ConsumerInfo(getConsumerId(), Destination.topic(topicName), ConsumerType.DURABLE_AT_LEAST_ONCE);
        Queue<SwallowMessage> queue = swallowBuffer.createMessageQueue(consumerInfo, tailMessageId, MessageFilter.createInSetMessageFilter(messageTypeSet));

        SwallowMessage m;
        while ((m = queue.poll()) == null) {
            sleep(10);
        }
        Assert.assertEquals("content2", m.getContent());
    }

	@Test
    public void testPoll2() throws InterruptedException {
		
        Set<String> messageTypeSet = new HashSet<String>();
        messageTypeSet.add(TYPE);
        ConsumerInfo consumerInfo = new ConsumerInfo(getConsumerId(), Destination.topic(topicName), ConsumerType.DURABLE_AT_LEAST_ONCE);
        Queue<SwallowMessage> queue = swallowBuffer.createMessageQueue(consumerInfo, tailMessageId, MessageFilter.createInSetMessageFilter(messageTypeSet));

        SwallowMessage m = queue.poll();
        while (m == null) {
        	sleep(10);
            m = queue.poll();
        }
        Assert.assertEquals("content2", m.getContent());
    }

    @Test
    public void testPoll3() throws InterruptedException {
        //插入1条消息
        String myType = TYPE + "_";
        SwallowMessage myTypeMsg = createMessage();
        myTypeMsg.setType(myType);
        messageDao.saveMessage(topicName, myTypeMsg);

        Set<String> messageTypeSet = new HashSet<String>();
        messageTypeSet.add(myType);
        ConsumerInfo consumerInfo = new ConsumerInfo(getConsumerId(), Destination.topic(topicName), ConsumerType.DURABLE_AT_LEAST_ONCE);
        Queue<SwallowMessage> queue = swallowBuffer.createMessageQueue(consumerInfo, tailMessageId, MessageFilter.createInSetMessageFilter(messageTypeSet));

        SwallowMessage m = queue.poll();
        while (m == null) {
        	sleep(10);
            m = queue.poll();
        }
        Assert.assertEquals(myType, m.getType());
    }
    
    @Override
    public SwallowMessage createMessage() {
    	
    	SwallowMessage swallowMessage = super.createMessage();
    	swallowMessage.setType(TYPE);
    	
    	return swallowMessage;
    }
    
}
