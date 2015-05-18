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
	public void testSaveMessage() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testSaveMessage");
		messageDAO.saveMessage(TOPIC_NAME, expectedMessage);
		// 查询消息是否正确
		SwallowMessage actualMessage = messageDAO.getMaxMessage(TOPIC_NAME);
		Assert.assertTrue(expectedMessage.equalsWithoutMessageId(actualMessage));
	}


	@Test
	public void testCleanMessage() {

		int messageCount = 100;

		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(TOPIC_NAME, null, 0L, messageCount).size());
		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(TOPIC_NAME, consumerId, 0L, messageCount).size());

		for (int i = 0; i < messageCount; i++) {
			if ((i & 1) == 1) {
				messageDAO.saveMessage(TOPIC_NAME, createMessage());
			} else {
				messageDAO.saveMessage(TOPIC_NAME, consumerId, createBackupMessage());
			}
		}

		Assert.assertEquals(messageCount / 2, messageDAO.getMessagesGreaterThan(TOPIC_NAME, null, 0L, messageCount)
				.size());
		Assert.assertEquals(messageCount / 2,
				messageDAO.getMessagesGreaterThan(TOPIC_NAME, consumerId, 0L, messageCount).size());

		messageDAO.cleanMessage(TOPIC_NAME, null);

		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(TOPIC_NAME, null, 0L, messageCount).size());
		Assert.assertEquals(messageCount / 2,
				messageDAO.getMessagesGreaterThan(TOPIC_NAME, consumerId, 0L, messageCount).size());

		messageDAO.cleanMessage(TOPIC_NAME, consumerId);

		for (int i = 0; i < messageCount / 2; i++) {
			messageDAO.saveMessage(TOPIC_NAME, createMessage());
		}

		Assert.assertEquals(messageCount / 2, messageDAO.getMessagesGreaterThan(TOPIC_NAME, null, 0L, messageCount)
				.size());
		Assert.assertEquals(0, messageDAO.getMessagesGreaterThan(TOPIC_NAME, consumerId, 0L, messageCount).size());

	}

	@Test
	public void testGetMessage() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testGetMessage");
		messageDAO.saveMessage(TOPIC_NAME, expectedMessage);
		// 查询消息是否正确
		Long maxMessageId = messageDAO.getMaxMessageId(TOPIC_NAME);
		SwallowMessage actualMessage = messageDAO.getMessage(TOPIC_NAME, maxMessageId);
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
		messageDAO.saveMessage(TOPIC_NAME, message);
		sleep(100);
		
		// 获取消息id
		Long maxMessageId = messageDAO.getMaxMessageId(TOPIC_NAME);
		Assert.assertNotNull(maxMessageId);
		// 再插入2条消息
		SwallowMessage expectedMessage1 = createMessage();
		messageDAO.saveMessage(TOPIC_NAME, expectedMessage1);
		SwallowMessage expectedMessage2 = createMessage();
		messageDAO.saveMessage(TOPIC_NAME, expectedMessage2);
		
		sleep(100);
		// 查询messageId比指定id大的按messageId升序排序的2条消息
		List<SwallowMessage> messagesGreaterThan = messageDAO.getMessagesGreaterThan(TOPIC_NAME, null, maxMessageId, 5);
		Assert.assertNotNull(messagesGreaterThan);
		Assert.assertEquals(2, messagesGreaterThan.size());
		Assert.assertTrue(expectedMessage1.equalsWithoutMessageId(messagesGreaterThan.get(0)));
		Assert.assertTrue(expectedMessage2.equalsWithoutMessageId(messagesGreaterThan.get(1)));
	}

}
