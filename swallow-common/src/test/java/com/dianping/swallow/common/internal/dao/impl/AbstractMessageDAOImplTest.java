package com.dianping.swallow.common.internal.dao.impl;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 * 
 *         2015年3月26日 下午2:40:39
 */
public abstract class AbstractMessageDAOImplTest extends AbstractDaoTest {

	protected MessageDAO<?> messageDao;

	protected int testCount = 20;
	
	@Before
	public void beforeMessageDAOImplTest() throws Exception {

		messageDao = createMessageDao();
	}

	protected abstract MessageDAO<?> createMessageDao() throws Exception;

	
	protected void insertMessage(String topicName){
		insertMessage(topicName, null, 1);
	}
	
	protected void insertMessage(String topicName, int count) {
		insertMessage(topicName, null, count);
	}

	protected void insertMessage(String topicName, String consumerId, int count) {

		for (int i = 0; i < count; i++) {

			messageDao.saveMessage(getTopic(), consumerId, createMessage());
		}
	}

	
	@Test
	public void testSaveMessage(){
		
		String topicName = getTopic();
		for(int i=0; i < testCount ; i++){
			
			SwallowMessage message1 = createMessage();
			messageDao.saveMessage(topicName, message1);
			
			SwallowMessage message2 = messageDao.getMaxMessage(getTopic());
			
			Assert.assertTrue(equals(message1, message2, false, false));
		}	
	}

	
	@Test
	public void testGetMessagesGreaterThan(){

		String topicName = getTopic();
		insertMessage(topicName);
		
		Long beginMessageId = messageDao.getMaxMessageId(topicName);
		
		
		int count = 100;
		insertMessage(topicName, count);
		
		ReturnMessageWrapper returnMessageWrapper = messageDao.getMessagesGreaterThan(topicName, null, beginMessageId, count + 1);
		
		Long currentMaxMessageId = messageDao.getMaxMessageId(topicName);
		
		Assert.assertEquals(currentMaxMessageId, returnMessageWrapper.getMaxMessageId());
		Assert.assertEquals(count, returnMessageWrapper.getRawMessageSize());
		Assert.assertEquals(count, returnMessageWrapper.getMessages().size());
	}


	
	@Test
	public void testAddAck(){

		String topicName = getTopic();
		String consumerId = getConsumerId();

		messageDao.cleanAck(topicName, consumerId);
		
		
		Long id = randomMessageId();
		messageDao.addAck(topicName, consumerId, id, "local");
		
		Long realId = messageDao.getAckMaxMessageId(topicName, consumerId);
		Assert.assertEquals(id, realId);;
	}
	
	protected abstract Long randomMessageId();

	@Test
	public void testAddBackupAck(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		
		messageDao.cleanAck(topicName, consumerId, true);
		
		Long messageId = randomMessageId();
		messageDao.addAck(topicName, consumerId, messageId, "", true);
		
		Long realId = messageDao.getAckMaxMessageId(topicName, consumerId, true);
		
		Assert.assertEquals(messageId, realId);
	}

	
	@Test
	public void testGetAccumulation(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		insertMessage(topicName);
		
		Long beginMessageId = messageDao.getMaxMessageId(topicName);
		messageDao.addAck(topicName, consumerId, beginMessageId, "ip");
		
		int count = 100;
		insertMessage(topicName, count);
				
		long accu = messageDao.getAccumulation(topicName, consumerId);
		Assert.assertEquals(count, accu);
	}
	

	@Test
	public void testCleanAck(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		
		messageDao.cleanAck(topicName, consumerId);
		Long ack = messageDao.getAckMaxMessageId(topicName, consumerId);
		
		Assert.assertNull(ack);
				
	}

	@Test
	public void testCleanBackupAck(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		
		messageDao.cleanAck(topicName, consumerId, true);
		Long ack = messageDao.getAckMaxMessageId(topicName, consumerId, true);
		
		Assert.assertNull(ack);
				
	}



}
