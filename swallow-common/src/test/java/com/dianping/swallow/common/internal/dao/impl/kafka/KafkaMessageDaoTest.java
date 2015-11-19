package com.dianping.swallow.common.internal.dao.impl.kafka;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.AbstractMessageDAOImplTest;
import com.dianping.swallow.common.internal.dao.impl.ReturnMessageWrapper;
import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月18日 下午7:29:06
 */
public class KafkaMessageDaoTest extends AbstractMessageDAOImplTest{
	
	protected String kafkaTopic = "UnittestTopicKafka";

	@Before
	public void beforeKafkaMessageDaoTest() throws Exception{
		
	}
	
	@Override
	protected MessageDAO<?> createMessageDao() throws Exception {

		KafkaConfig kafkaConfig = new KafkaConfig("swallow-kafka.properties", null, false);
		kafkaConfig.setZip("none");
		
		KafkaCluster cluster = new KafkaCluster(getDbAddress(), kafkaConfig);
		cluster.setSwallowConfig(getSwallowConfig());
		
		cluster.initialize();
		KafkaMessageDao messageDao  = new KafkaMessageDao(cluster);
		return messageDao;
	}

	@Test
	public void testSaveBackupMessage(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		for(int i=0; i < testCount ; i++){
			
			SwallowMessage message1 = createMessage();
			messageDao.saveMessage(topicName, consumerId, message1);
			
			SwallowMessage message2 = messageDao.getMaxMessage(KafkaMessageDao.BACKUP_TOPIC);
			
			Assert.assertEquals(topicName, message2.getInternalProperty(InternalProperties.TOPIC));
			Assert.assertEquals(consumerId, message2.getInternalProperty(InternalProperties.CONSUMERID));
			Assert.assertTrue(equals(message1, message2));
		}	
	}

	
	@Test
	public void testGetMaxMessageId(){
		
		String topicName = getTopic();
		String consumerId = getConsumerId();
		messageDao.saveMessage(topicName, consumerId, createMessage());
		
		Long maxBackupMessageId = messageDao.getMaxMessageId(topicName, consumerId);
		
		SwallowMessage message = messageDao.getMaxMessage(KafkaMessageDao.BACKUP_TOPIC);
		
		Assert.assertEquals(message.getMessageId(), maxBackupMessageId);
		
	}

	@Test
	public void testBackupGetMessagesGreaterThan(){

		String topicName = getTopic();
		String consumerId = getConsumerId();
		
		insertMessage(KafkaMessageDao.BACKUP_TOPIC, 1);
		
		Long beginMessageId = messageDao.getMaxMessageId(KafkaMessageDao.BACKUP_TOPIC);
		
		
		int count = 100;
		for(int i=0; i < count; i++){
			
			insertMessage(topicName, consumerId, 1);
			insertMessage(topicName,  randomString(), 1);
		}
		
		ReturnMessageWrapper returnMessageWrapper = messageDao.getMessagesGreaterThan(topicName, consumerId, beginMessageId, 1);
		
		Long currentMaxMessageId = messageDao.getMaxMessageId(KafkaMessageDao.BACKUP_TOPIC);
		
		Assert.assertEquals(currentMaxMessageId, returnMessageWrapper.getMaxMessageId());
		Assert.assertEquals(count * 2, returnMessageWrapper.getRawMessageSize());
		Assert.assertEquals(count, returnMessageWrapper.getMessages().size());
	}

	
	@Override
	protected String getTopic() {
		return kafkaTopic;
	}

	@Override
	protected String getDbAddress() {
		return getKafkaAddress();
	}

	@Override
	protected Long randomMessageId() {
		return randomLong();
	}

}
