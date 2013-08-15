package com.dianping.swallow.example.loadtest;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class ConsumerRunner {

    private static int topicCount    = 2;
    private static int consumerCount = 10;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < topicCount; i++) {
            String topic = "LoadTestTopic-" + i;
            for (int j = 0; j < consumerCount; j++) {
                ConsumerConfig config = new ConsumerConfig();
                //以下两项根据自己情况而定，默认是不需要配的
                config.setThreadPoolSize(2);
                config.setRetryCountOnBackoutMessageException(0);
                Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic(topic), "myId-20130813", config);
                c.setListener(new MessageListener() {
                    @Override
                    public void onMessage(Message msg) {
//                        System.out.println("延迟" + (System.currentTimeMillis() - msg.getGeneratedTime().getTime()) + "ms");
                    }
                });
                c.start();
            }
        }
    }

}
