package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.mongodb.MongoException;

public class MessageDAOImplTest extends AbstractMessageDAOImplTest {


	protected String getConsumerId() {
		return null;
	}

	
	@Test
	public void getAccumulation(){
		
		int saveCount = 50;
		long size = messageDAO.getAccumulation(topicName, consumerId);
		
		Assert.assertEquals(0, size);
		
		insertMessage(saveCount, topicName);
		
		SwallowMessage message = messageDAO.getMaxMessage(topicName);
		messageDAO.addAck(topicName, consumerId, message.getMessageId(), consumerId);

		insertMessage(saveCount, topicName);
		
		size = messageDAO.getAccumulation(topicName, consumerId);
		Assert.assertEquals(saveCount, size);
		
	}

	@Test
	public void testSaveMessage() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testSaveMessage");
		messageDAO.saveMessage(topicName, expectedMessage);
		// 查询消息是否正确
		SwallowMessage actualMessage = messageDAO.getMaxMessage(topicName);
		Assert.assertTrue(expectedMessage.equalsWithoutMessageId(actualMessage));
	}


	@Test
	public void testCleanMessage() {

		int messageCount = 100;

		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(topicName, null, 0L, messageCount).size());
		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(topicName, consumerId, 0L, messageCount).size());

		for (int i = 0; i < messageCount; i++) {
			if ((i & 1) == 1) {
				messageDAO.saveMessage(topicName, createMessage());
			} else {
				messageDAO.saveMessage(topicName, consumerId, createBackupMessage());
			}
		}

		Assert.assertEquals(messageCount / 2, messageDAO.getMessagesGreaterThan(topicName, null, 0L, messageCount)
				.size());
		Assert.assertEquals(messageCount / 2,
				messageDAO.getMessagesGreaterThan(topicName, consumerId, 0L, messageCount).size());

		messageDAO.cleanMessage(topicName, null);

		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(topicName, null, 0L, messageCount).size());
		Assert.assertEquals(messageCount / 2,
				messageDAO.getMessagesGreaterThan(topicName, consumerId, 0L, messageCount).size());

		messageDAO.cleanMessage(topicName, consumerId);

		for (int i = 0; i < messageCount / 2; i++) {
			messageDAO.saveMessage(topicName, createMessage());
		}

		Assert.assertEquals(messageCount / 2, messageDAO.getMessagesGreaterThan(topicName, null, 0L, messageCount)
				.size());
		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(topicName, consumerId, 0L, messageCount).size());

	}

	@Test
	public void testGetMessage() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testGetMessage");
		messageDAO.saveMessage(topicName, expectedMessage);
		// 查询消息是否正确
		Long maxMessageId = messageDAO.getMaxMessageId(topicName);
		SwallowMessage actualMessage = messageDAO.getMessage(topicName, maxMessageId);
		Assert.assertTrue(expectedMessage.equalsWithoutMessageId(actualMessage));
	}

	@Test(expected = MongoException.class)
	public void testSwallowDifferentDbName() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testGetMessage");
		messageDAO.saveMessage("test", expectedMessage);
		messageDAO.saveMessage("Test", expectedMessage);
	}

	@Test
	public void testGetMessagesGreaterThan() {
		
		// 插入1条消息
		SwallowMessage message = createMessage();
		messageDAO.saveMessage(topicName, message);
		sleep(100);
		
		// 获取消息id
		Long maxMessageId = messageDAO.getMaxMessageId(topicName);
		Assert.assertNotNull(maxMessageId);
		// 再插入2条消息
		SwallowMessage expectedMessage1 = createMessage();
		messageDAO.saveMessage(topicName, expectedMessage1);
		SwallowMessage expectedMessage2 = createMessage();
		messageDAO.saveMessage(topicName, expectedMessage2);
		
		sleep(100);
		// 查询messageId比指定id大的按messageId升序排序的2条消息
		List<SwallowMessage> messagesGreaterThan = messageDAO.getMessagesGreaterThan(topicName, null, maxMessageId, 5);
		Assert.assertNotNull(messagesGreaterThan);
		Assert.assertEquals(2, messagesGreaterThan.size());
		Assert.assertTrue(expectedMessage1.equalsWithoutMessageId(messagesGreaterThan.get(0)));
		Assert.assertTrue(expectedMessage2.equalsWithoutMessageId(messagesGreaterThan.get(1)));
	}

}
