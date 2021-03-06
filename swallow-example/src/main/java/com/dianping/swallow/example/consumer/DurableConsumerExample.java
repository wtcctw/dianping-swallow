package com.dianping.swallow.example.consumer;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @rundemo_name 消费者例子(持久)
 */
public class DurableConsumerExample {

    public static void main(String[] args) throws InterruptedException {
        ConsumerConfig config = new ConsumerConfig();
        config.setThreadPoolSize(50);
        config.setRetryCount(0);

        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), "myId1", config);
        c.setListener(new MessageListener() {

            @Override
            public void onMessage(Message msg) {
                System.out.println("延迟" + (System.currentTimeMillis() - msg.getGeneratedTime().getTime()) + "ms");
                System.out.println(msg.getContent());
            }
        });
        c.start();
        
        Thread.sleep(30000);
        c.close();
        Thread.sleep(10000);
        c.start();
        
    }

}
