package com.dianping.test.swallow.log4j2;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import com.dianping.swallow.test.load.AbstractLoadTest;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class ConsumerRunner extends AbstractLoadTest {

    public static void main(String[] args) {
        ConsumerConfig config = new ConsumerConfig();
        //以下两项根据自己情况而定，默认是不需要配的
        config.setThreadPoolSize(1);

        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("product-kafka-test"), "myId-product-kafka-test", config);
        c.setListener(new MessageListener() {

            @Override
            public void onMessage(Message msg) {
                System.out.println(msg.getContent());
            }
        });
        c.start();
    }
}