package com.dianping.swallow.example.consumer.requestid;

import com.dianping.phoenix.environment.PhoenixContext;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 */
public class RequestIdConsumerExample {

    public static void main(String[] args) {
        ConsumerConfig config = new ConsumerConfig();
        //以下两项根据自己情况而定，默认是不需要配的
        config.setThreadPoolSize(1);
        config.setConsumerType(ConsumerType.NON_DURABLE);
        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), config);
        c.setListener(new MessageListener() {

            @Override
            public void onMessage(Message msg) {
                System.out.println("延迟" + (System.currentTimeMillis() - msg.getGeneratedTime().getTime()) + "ms");
                System.out.println(msg.getContent());
                System.out.println(PhoenixContext.getInstance().getRequestId());
                System.out.println(PhoenixContext.getInstance().getReferRequestId());
                System.out.println(PhoenixContext.getInstance().getGuid());
                //            System.out.println(msg.transferContentToBean(MsgClass.class));
            }
        });
        c.start();
    }

}
