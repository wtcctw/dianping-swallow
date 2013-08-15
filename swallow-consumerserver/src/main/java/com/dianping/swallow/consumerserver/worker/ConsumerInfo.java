package com.dianping.swallow.consumerserver.worker;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.message.Destination;

public class ConsumerInfo {

   private String       consumerId;
   private Destination  dest;
   private ConsumerType consumerType;

   public ConsumerInfo(String consumerId, Destination dest, ConsumerType consumerType) {
      super();
      this.consumerId = consumerId;
      this.dest = dest;
      this.consumerType = consumerType;
   }

   public String getConsumerId() {
      return consumerId;
   }

   public void setConsumerId(String consumerId) {
      this.consumerId = consumerId;
   }

   public Destination getDest() {
      return dest;
   }

   public void setDest(Destination dest) {
      this.dest = dest;
   }

   public ConsumerType getConsumerType() {
      return consumerType;
   }

   public void setConsumerType(ConsumerType consumerType) {
      this.consumerType = consumerType;
   }

   /**
    * 以topic和consumerId为主键
    */
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
      result = prime * result + ((dest.getName() == null) ? 0 : dest.getName().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ConsumerInfo other = (ConsumerInfo) obj;
      if (consumerId == null) {
         if (other.consumerId != null)
            return false;
      } else if (!consumerId.equals(other.consumerId))
         return false;
      if (dest.getName() == null) {
         if (other.dest.getName() != null)
            return false;
      } else if (!dest.getName().equals(other.dest.getName()))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ConsumerInfo [consumerId=" + consumerId + ", dest=" + dest + ", consumerType=" + consumerType + "]";
   }

}
