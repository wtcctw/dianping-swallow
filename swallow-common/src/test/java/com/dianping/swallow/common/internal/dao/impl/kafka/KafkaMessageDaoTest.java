package com.dianping.swallow.common.internal.dao.impl.kafka;

import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.AbstractDbTest;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月18日 下午7:29:06
 */
public class KafkaMessageDaoTest extends AbstractDbTest{
	
	private KafkaMessageDao messageDao;
	
	@Before
	public void beforeKafkaMessageDaoTest() throws Exception{
		
		KafkaCluster cluster = new KafkaCluster(getKafkaAddress(), new KafkaConfig("swallow-kafka.properties", null));
		cluster.initialize();
		messageDao  = new KafkaMessageDao(cluster);
	}

	@Test
	public void testSaveMessage(){
		
		SwallowMessage swallowMessage = createMessage();
		messageDao.saveMessage(topicName, createMessage());
	}
	
	

}
