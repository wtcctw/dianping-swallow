package com.dianping.swallow.common.internal.dao.impl.mongodb;

import java.util.concurrent.atomic.AtomicInteger;

import org.bson.types.BSONTimestamp;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.MongoUtils;

/**
 * @author mengwenchao
 * 
 *         2015年3月26日 下午2:40:39
 */
public abstract class AbstractMessageDAOImplTest extends AbstractDAOImplTest {

	protected MessageDAO<?> messageDAO;

	protected String consumerId = "consumer1";

	@Before
	public void beforeMessageDAOImplTest() {
		
		messageDAO = getBean(MessageDAO.class);
				
		messageDAO.cleanMessage(topicName, null);
		messageDAO.cleanMessage(topicName, consumerId);
	}

//	@Test
	//删除此测试，collection为capped
	public void testDeleteMessage() {
		
		Assert.assertEquals(0, messageDAO.count(topicName, getConsumerId()));
		
		int count1 = 100, count2 = 150;
		
		insertMessage(count1, topicName, getConsumerId());
		
		Assert.assertEquals(count1, messageDAO.count(topicName, getConsumerId()));
		
		Long currentMaxId = messageDAO.getMaxMessageId(topicName, getConsumerId());
		
		insertMessage(count2, topicName, getConsumerId());
		
		Assert.assertEquals(count2 + count1, messageDAO.count(topicName, getConsumerId()));
		
		int del = messageDAO.deleteMessage(topicName, getConsumerId(), currentMaxId + 1);
		
		Assert.assertEquals(count1, del);
		
		Assert.assertEquals(count2, messageDAO.count(topicName, getConsumerId()));
		
	}


	protected abstract String getConsumerId();

	protected void insertMessage(int count, String topicName) {
		insertMessage(count, topicName, null);
	}

	protected void insertMessage(int count, String topicName, String consumerId) {

		for (int i = 0; i < count; i++) {

			messageDAO.saveMessage(topicName, consumerId, createBackupMessage());
		}
	}

	private AtomicInteger inc = new AtomicInteger();

	protected SwallowMessage createBackupMessage() {
		SwallowMessage message = createMessage();
		message.setMessageId(MongoUtils.BSONTimestampToLong(new BSONTimestamp(
				(int) (System.currentTimeMillis() / 1000), inc.incrementAndGet())));
		return message;

	}

	
   @Test
   public void testAdd() {
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      messageDAO.addAck(topicName, getConsumerId(), MongoUtils.BSONTimestampToLong(timestamp), IP);
      
      //测试
      Long maxMessageId = messageDAO.getAckMaxMessageId(topicName, getConsumerId());
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

   @Test
   public void testGetMaxMessageId() {
	   
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      messageDAO.addAck(topicName, getConsumerId(), MongoUtils.BSONTimestampToLong(timestamp), IP);
      //测试
      Long maxMessageId = messageDAO.getAckMaxMessageId(topicName, getConsumerId());
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

}
