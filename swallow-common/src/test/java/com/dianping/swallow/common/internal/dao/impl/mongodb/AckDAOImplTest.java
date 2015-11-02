package com.dianping.swallow.common.internal.dao.impl.mongodb;


import org.bson.types.BSONTimestamp;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoAckDAO;
import com.dianping.swallow.common.internal.util.MongoUtils;

public class AckDAOImplTest extends AbstractDAOImplTest {

   private MongoAckDAO ackDAO;
   
   @Before
   public void beforeAckDAOImplTest(){
	   ackDAO = getBean(MongoAckDAO.class);
   }

   @Test
   public void testAdd() {
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      ackDAO.add(topicName, getConsumerId(), MongoUtils.BSONTimestampToLong(timestamp), IP);
      
      //测试
      Long maxMessageId = ackDAO.getMaxMessageId(topicName, getConsumerId());
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

   @Test
   public void testGetMaxMessageId() {
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      ackDAO.add(topicName, getConsumerId(), MongoUtils.BSONTimestampToLong(timestamp), IP);
      //测试
      Long maxMessageId = ackDAO.getMaxMessageId(topicName, getConsumerId());
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

}
