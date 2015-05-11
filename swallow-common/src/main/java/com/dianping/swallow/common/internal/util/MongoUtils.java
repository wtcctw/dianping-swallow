package com.dianping.swallow.common.internal.util;

import java.util.Date;

import org.bson.types.BSONTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MongoUtils {
	
   protected  static final Logger logger     = LoggerFactory.getLogger(MongoUtils.class);
	
   private MongoUtils() {
   }

   public static BSONTimestamp longToBSONTimestamp(Long messageId) {
      int time = (int) (messageId >>> 32);
      int inc = (int) (messageId & 0xFFFFFFFF);
      BSONTimestamp timestamp = new BSONTimestamp(time, inc);
      return timestamp;
   }

   public static Long BSONTimestampToLong(BSONTimestamp timestamp) {
      int time = timestamp.getTime();
      int inc = timestamp.getInc();
      Long messageId = ((long) time << 32) | inc;
      return messageId;
   }

   public static Long getLongByCurTime() {
      return getLongByDate(new Date());
   }
   
   public static Long getLongByDate(Date date) {
	      int time = (int) (date.getTime() / 1000);
	      BSONTimestamp bst = new BSONTimestamp(time, 0);
	      if(logger.isDebugEnabled()){
	    	  logger.debug("[getLongByDate][BSONTimestamp]" + bst);
	      }
	      Long result = BSONTimestampToLong(bst);
	      if(logger.isDebugEnabled()){
	    	  logger.debug("[getLongByDate][messageId]" + result);
	      }
	      return result;
	   }

   
   public static void main(String[] args) {
	   
	   System.out.println(-1 << 32);
	   
      System.out.println(MongoUtils.longToBSONTimestamp(5759378400351354888L));
      BSONTimestamp ts = new BSONTimestamp(1376825482,573);
      System.out.println(new Date(ts.getTime()*1000L));
      System.out.println(Integer.MAX_VALUE/86400/30/12);
   }
}
