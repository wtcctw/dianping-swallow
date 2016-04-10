package com.dianping.swallow.consumerserver.buffer;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.AbstractConsumerServerSpringTest;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerWorkerManager;

public class ConsumerWorkerImplTest extends AbstractConsumerServerSpringTest {
	
	private String IP = "127.0.0.1";
	
    private ConsumerWorkerManager consumerWorkerManager;
    
    private Set<SwallowMessage>   messageSetChecker = new HashSet<SwallowMessage>();

    private Channel               channel;
    
    @Before
    public void testConsumerWorkerImplTest(){
    	
    	consumerWorkerManager = getBean(ConsumerWorkerManager.class);
    	
    }

    private void makeMessages(Queue<SwallowMessage> messageQueue) {
        for (long i = 0; i < 50; i++) {
            SwallowMessage message = new SwallowMessage();
            message.setMessageId(i);
            messageQueue.add(message);
        }

    }

    @Before
    public void mockDao() throws Exception {
    	
        SwallowBuffer swallowBuffer = mock(SwallowBuffer.class);
        CloseableBlockingQueue<SwallowMessage> messageQueue = new MockedCloseableBlockingQueue<SwallowMessage>();

        makeMessages(messageQueue);
        when(swallowBuffer.createMessageQueue(Matchers.any(ConsumerInfo.class), Matchers.anyLong())).thenReturn(messageQueue);
        //      AckDAO ackDAO = mock(AckDAO.class);
        //      //doReturn(print()).when(ackDAO).add(Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyString());
        //      MessageDAO messageDAO = mock(MessageDAO.class);
        //      when(ackDAO.getMaxMessageId(TOPIC_NAME, CONSUMER_ID)).thenReturn(123456L);
        //      when(ackDAO.getMaxMessageId(TOPIC_NAME2, CONSUMER_ID)).thenReturn(null);
        //      when(ackDAO.getMaxMessageId(TOPIC_NAME, CONSUMER_ID2)).thenReturn(null);
        //      doAnswer(new Answer<Object>() {
        //         @Override
        //         public String answer(InvocationOnMock invocation) throws Throwable {
        //            System.out.println("RUN ackDAO.add()!");
        //            return "hello";
        //         }
        //      }).when(ackDAO).add(Matchers.anyString(), Matchers.anyString(), Matchers.anyLong(), Matchers.anyString());
        //      when(messageDAO.getMaxMessageId(TOPIC_NAME)).thenReturn(234567L);
        //      when(messageDAO.getMaxMessageId(TOPIC_NAME2)).thenReturn(null);
        //准备数据
        messageDao.addAck(getTopic(), getConsumerId(), 123456L, IP);
        SwallowMessage message = new SwallowMessage();
        message.setContent("this is a SwallowMessage");
        messageDao.saveMessage(getTopic(), message);

        //      consumerWorkerManager.setAckDAO(ackDAO);
        //      consumerWorkerManager.setMessageDAO(messageDAO);
        consumerWorkerManager.setSwallowBuffer(swallowBuffer);
        consumerWorkerManager.initialize();;
        consumerWorkerManager.start();
    }

    @Before
    public void mockChannel() {
        channel = mock(Channel.class);
        when(channel.remoteAddress()).thenReturn(new InetSocketAddress(IP, 8081));
        when(channel.isActive()).thenReturn(true);
        when(channel.write(argThat(new Matcher<Object>() {
            @Override
            public void describeTo(Description arg0) {

            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {

            }

            @Override
            public boolean matches(Object arg0) {
                messageSetChecker.add(((PktMessage) arg0).getContent());
                return true;
            }

			//@Override
			public void describeMismatch(Object item,
					Description mismatchDescription) {
				// TODO Auto-generated method stub
				
			}
        }))).thenReturn(null);
    }

    /**
     * consumerType 为NON_DURABLE
     * 
     * @throws InterruptedException
     */
    @Test
    public void testHandleGreet_NON_DURABLE() throws InterruptedException {
    	
        ConsumerInfo consumerInfo2 = new ConsumerInfo(getConsumerId(), Destination.topic(getTopic()), ConsumerType.NON_DURABLE);
        consumerWorkerManager.handleGreet(channel, consumerInfo2, 50, null, -1);
    }

    /**
     * topic为xxx,xxx还没有消息
     * 
     * @throws InterruptedException
     */
    @Test
    public void testHandleGreet_topicFirst() throws InterruptedException {
        //      mockChannel();
        //      mockDao();
        ConsumerInfo consumerInfo3 = new ConsumerInfo(getConsumerId(), Destination.topic(getTopic()), ConsumerType.DURABLE_AT_LEAST_ONCE);
        consumerWorkerManager.handleGreet(channel, consumerInfo3, 50, null, -1);
        Thread.sleep(3000);
        //      Assert.assertTrue(check(50));
    }

    /**
     * topic为xx，consumerId为dp11的第一次访问，但xx已经有消息产生了
     * 
     * @throws InterruptedException
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testHandleGreet_consumerFirst() throws InterruptedException {
        //      mockChannel();
        //      mockDao();
        ConsumerInfo consumerInfo2 = new ConsumerInfo(getConsumerId(), Destination.topic(getTopic()), ConsumerType.DURABLE_AT_MOST_ONCE);
        consumerWorkerManager.handleGreet(channel, consumerInfo2, 50, null, -1);
        Thread.sleep(3000);
        //      Assert.assertTrue(check(50));
    }

    /**
     * topic为xx，consumerId为dp1。从greet到ack,再到最后disconnect
     * 
     * @throws InterruptedException
     */
    @Test
    public void testHandleGreet() throws InterruptedException {

        //      mockChannel();
        //      mockDao();

        ConsumerInfo consumerInfo1 = new ConsumerInfo(getConsumerId(), Destination.topic(getTopic()), ConsumerType.DURABLE_AT_LEAST_ONCE);
        consumerWorkerManager.handleGreet(channel, consumerInfo1, 30, null, -1);
        //        Thread.sleep(3000);
        //        Assert.assertTrue(check(30));
        //        Assert.assertEquals(30,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getWaitAckMessages().get(channel).size());

        consumerWorkerManager.handleAck(channel, consumerInfo1, 20L, ACKHandlerType.SEND_MESSAGE);
        //        Thread.sleep(3000);
        //        Assert.assertEquals(30,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getWaitAckMessages().get(channel).size());
        //        Assert.assertTrue(check(31));
        //        Assert.assertEquals(0,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getMessagesToBeSend().size());

        consumerWorkerManager.handleAck(channel, consumerInfo1, 18L, ACKHandlerType.NO_SEND);
        //        Thread.sleep(2000);
        //        Assert.assertEquals(29,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getWaitAckMessages().get(channel).size());
        //        Assert.assertTrue(check(31));
        //        Assert.assertEquals(0,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getMessagesToBeSend().size());

        //ACKHandlerType.CLOSE_CHANNEL需要netty才能触发正常逻辑，故无法测试
        //      consumerWorkerManager.handleAck(channel, consumerInfo1, 19L, ACKHandlerType.CLOSE_CHANNEL);
        //      Thread.sleep(2000);
        //      Assert.assertEquals(1, ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker()
        //            .get(consumerId1)).getConnectedChannels().size());
        //      Assert.assertEquals(null,
        //            ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                  .getWaitAckMessages().get(channel));
        //      Assert.assertEquals(28,
        //            ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                  .getCachedMessages().size());

        consumerWorkerManager.handleGreet(channel, consumerInfo1, 30, null, -1);
        //        Thread.sleep(3000);
        //      Assert.assertTrue(check(51));
        //        Assert.assertEquals(48,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getWaitAckMessages().get(channel).size());
        //        Assert.assertEquals(0,
        //                ((ConsumerWorkerImpl) consumerWorkerManager.getConsumerId2ConsumerWorker().get(consumerId1))
        //                        .getMessagesToBeSend().size());
    }

    static class MockedCloseableBlockingQueue<E> extends LinkedBlockingQueue<E> implements CloseableBlockingQueue<E> {
        private static final long serialVersionUID = 1L;

        @Override
        public void close() {
        }

        @Override
        public void isClosed() {
        }

		@Override
		public Long getEmptyTailMessageId() {
			return null;
		}


		@Override
		public void putMessage(List<SwallowMessage> messages) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setTailMessageId(Long tailId) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Long getTailMessageId() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setMessageRetriever(MessageRetriever messageRetriever) {
			
		}

    }

}
