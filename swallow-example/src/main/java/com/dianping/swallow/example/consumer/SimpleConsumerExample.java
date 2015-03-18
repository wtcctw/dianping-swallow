package com.dianping.swallow.example.consumer;

import java.util.concurrent.atomic.AtomicInteger;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

public class SimpleConsumerExample {

    public static void main(String[] args) {
    	
        final AtomicInteger count = new AtomicInteger();
        //以下两项根据自己情况而定，默认是不需要配的
        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(10);

        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), "myId2", config);
//      Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), "myId2", config);
        c.setListener(new MessageListener() {

            @Override
            public void onMessage(Message msg) {
                System.out.println("延迟" + (System.currentTimeMillis() - msg.getGeneratedTime().getTime()) + "ms");
                System.out.println(msg.getContent());
                System.out.println(count.incrementAndGet());
                //            System.out.println(msg.transferContentToBean(MsgClass.class));
            }
        });
        c.start();
        
    }
}
