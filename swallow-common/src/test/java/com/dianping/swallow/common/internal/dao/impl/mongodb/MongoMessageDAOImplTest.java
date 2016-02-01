package com.dianping.swallow.common.internal.dao.impl.mongodb;


import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDAOImplTest;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.mongodb.MongoException;

public class MongoMessageDAOImplTest extends AbstractMessageDAOImplTest {

	@Override
	protected MessageDAO<?> createMessageDao() throws Exception {

		 MongoConfig mongoConfig = new MongoConfig("swallow-mongo.properties", null, false); 
		
		MongoCluster cluster = new MongoCluster(mongoConfig.buildMongoOptions(), getDbAddress());
		cluster.setSwallowServerConfig(getSwallowServerConfig());
		
		cluster.initialize();
		
		MongoMessageDAO messageDao  = new MongoMessageDAO(cluster);
		return messageDao;
	}

	@Test
	public void testCleanMessage() {

		messageDao.cleanMessage(getTopic(), null);
		messageDao.cleanMessage(getTopic(), getConsumerId());
		
		int messageCount = 100;

		Assert.assertEquals(0, messageDao.getMessagesGreaterThan(getTopic(), null, 0L, messageCount).getMessages().size());
		Assert.assertEquals(0, messageDao.getMessagesGreaterThan(getTopic(), getConsumerId(), 0L, messageCount).getMessages().size());

		for (int i = 0; i < messageCount; i++) {
			if ((i & 1) == 1) {
				messageDao.saveMessage(getTopic(), createMessage());
			} else {
				messageDao.saveMessage(getTopic(), getConsumerId(), createMessage());
			}
		}

		Assert.assertEquals(messageCount / 2, messageDao.getMessagesGreaterThan(getTopic(), null, 0L, messageCount).getMessages()
				.size());
		Assert.assertEquals(messageCount / 2,
				messageDao.getMessagesGreaterThan(getTopic(), getConsumerId(), 0L, messageCount).getMessages().size());

		messageDao.cleanMessage(getTopic(), null);

		Assert.assertEquals(0, messageDao.getMessagesGreaterThan(getTopic(), null, 0L, messageCount).getMessages().size());
		Assert.assertEquals(messageCount / 2,
				messageDao.getMessagesGreaterThan(getTopic(), getConsumerId(), 0L, messageCount).getMessages().size());

		messageDao.cleanMessage(getTopic(), getConsumerId());

		for (int i = 0; i < messageCount / 2; i++) {
			messageDao.saveMessage(getTopic(), createMessage());
		}

		Assert.assertEquals(messageCount / 2, messageDao.getMessagesGreaterThan(getTopic(), null, 0L, messageCount)
				.getMessages().size());
		Assert.assertEquals(0, messageDao.getMessagesGreaterThan(getTopic(), getConsumerId(), 0L, messageCount).getMessages().size());

	}

	@Test(expected = MongoException.class)
	public void testSwallowDifferentDbName() {
		// 插入消息
		SwallowMessage expectedMessage = createMessage();
		expectedMessage.setContent("content in testGetMessage");
		messageDao.saveMessage("test", expectedMessage);
		messageDao.saveMessage("Test", expectedMessage);
	}

	@Override
	protected String getDbAddress() {
		return getMongoAddress();
	}

	@Override
	protected Long randomMessageId() {
		return MongoUtils.getLongByCurTime();
	}
}
