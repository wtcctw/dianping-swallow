package com.dianping.swallow.common.internal.dao.impl.mongodb;


import org.bson.types.BSONTimestamp;
import org.junit.Before;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.mongodb.AckDAOImpl;
import com.dianping.swallow.common.internal.util.MongoUtils;

public class AckDAOImplTest extends AbstractDAOImplTest {

   private AckDAOImpl ackDAO;
   
   @Before
   public void beforeAckDAOImplTest(){
	   ackDAO = getBean(AckDAOImpl.class);
   }

   @Test
   public void testAdd() {
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      ackDAO.add(TOPIC_NAME, CONSUMER_ID, MongoUtils.BSONTimestampToLong(timestamp), IP);
      //测试
      Long maxMessageId = ackDAO.getMaxMessageId(TOPIC_NAME, CONSUMER_ID);
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

   @Test
   public void testGetMaxMessageId() {
      //添加一条记录
      int time = (int) (System.currentTimeMillis() / 1000);
      int inc = 1;
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      Long expectedMessageId = MongoUtils.BSONTimestampToLong(timestamp);
      ackDAO.add(TOPIC_NAME, CONSUMER_ID, MongoUtils.BSONTimestampToLong(timestamp), IP);
      //测试
      Long maxMessageId = ackDAO.getMaxMessageId(TOPIC_NAME, CONSUMER_ID);
      Assert.assertEquals(expectedMessageId, maxMessageId);
   }

}
