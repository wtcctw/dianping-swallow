package com.dianping.swallow.kafka.zk;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.kafka.AbstractKafkaTest;
import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.zookeeper.ZkUtils;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 下午5:42:37
 */
public class ZkUtilsTest extends AbstractKafkaTest{
	
	private ZkUtils zkUtils;
	
	@Before
	public void beforeZkUtilsTest(){
		zkUtils = new ZkUtils(getZkAddress());
	}
	
	
	@Test
	public void testBackupGet(){
		
		System.out.println(zkUtils.getBackupAck(new TopicAndPartition("randomTopic"), "randomGroup"));
		
	}
	
	@Test
	public void testBackup(){

		TopicAndPartition tp = new TopicAndPartition(getTopic(), 0);
		String groupId = "group1";
		Long ack = 11L;
		int count = 100;
		
		Long begin = System.currentTimeMillis();
		
		for(int i =0; i < count ;i++){
			
			zkUtils.saveBackupAck(tp, groupId, ack);
			
			Long resultAck = zkUtils.getBackupAck(tp, groupId);
			
			Assert.assertEquals(ack, resultAck);
			
			ack = ++resultAck;
		}

		Long end = System.currentTimeMillis();
		System.out.println("Time:" + (end - begin) + ", each:" + (end - begin)/count);
	}
	
	
	

}
