package com.dianping.swallow.common.internal.util;

import java.util.UUID;

public class ConsumerIdUtil {

   /**
    * 为非持久的Consumer生成唯一consumerId. 非持久的id以#开头，持久的id不会以#开头。
    */
   public static String getRandomNonDurableConsumerId() {
      return "#" + UUID.randomUUID().toString();
   }

   /**
    * 判断consumerId是否为非持久的id. 非持久的id以#开头，持久的id不会以#开头。
    */
   public static boolean isNonDurableConsumerId(String consumerId) {
      if (consumerId == null) {
         return false;
      }
      return consumerId.startsWith("#");
   }

}
