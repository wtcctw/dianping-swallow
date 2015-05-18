package com.dianping.swallow.common.internal.dao.impl.mongodb;


import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.common.internal.dao.impl.mongodb.HeartbeatDAOImpl;

public class HeartbeatDAOImplTest extends AbstractDAOImplTest {

   private HeartbeatDAOImpl heartbeatDAO;
   
   @Before
   public void beforeHeartbeatDAOImplTest(){

	   heartbeatDAO = getBean(HeartbeatDAOImpl.class);
   }

   @Test
   public void testUpdateLastHeartbeat() {
      Date expectedDate = heartbeatDAO.updateLastHeartbeat(IP);

      Date actualDate = heartbeatDAO.findLastHeartbeat(IP);
      Assert.assertEquals(expectedDate, actualDate);

   }

   @Test
   public void testFindLastHeartbeat() {
      Date expectedDate = heartbeatDAO.updateLastHeartbeat(IP);

      Date actualDate = heartbeatDAO.findLastHeartbeat(IP);
      Assert.assertEquals(expectedDate, actualDate);

   }

}
