package com.dianping.swallow.common.internal.consumer;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.message.Destination;

public class ConsumerInfo extends ConsumerId{

   private ConsumerType consumerType;

   public ConsumerInfo(String consumerId, Destination dest, ConsumerType consumerType) {
      super(consumerId, dest);
      this.consumerType = consumerType;
   }

   public ConsumerType getConsumerType() {
      return consumerType;
   }

   public void setConsumerType(ConsumerType consumerType) {
      this.consumerType = consumerType;
   }
   
   public ConsumerId createConsumerId(){
	   
	   return new ConsumerId(getConsumerId(), getDest());
   }
   

   @Override
   public String toString() {
      return "ConsumerInfo [cid=" + getConsumerId() + ", dest=" + getDest() + ", type=" + consumerType + "]";
   }

}
