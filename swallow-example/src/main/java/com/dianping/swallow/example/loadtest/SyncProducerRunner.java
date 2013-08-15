package com.dianping.swallow.example.loadtest;

import java.util.Date;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class SyncProducerRunner {

    public static final int QPS           = 20;  //每个Producer发送消息的qps
    private static int      topicCount    = 2;
    private static int      producerCount = 10;

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < topicCount; i++) {
            String topic = "LoadTestTopic-" + i;
            for (int j = 0; j < producerCount; j++) {
                ProduceRunner runner = new ProduceRunner(topic);
                runner.start();
            }
        }
    }

    static class ProduceRunner extends Thread {
        String topic;

        private ProduceRunner(String topic) {
            this.topic = topic;
        }

        @Override
        public void run() {
            try {
                ProducerConfig config = new ProducerConfig();
                config.setMode(ProducerMode.SYNC_MODE);
                Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);

                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    try {
                        String msg = SyncProducerRunner.class.getSimpleName() + "(" + (new Date()) + "): 消息-" + i;
                        producer.sendMessage(msg);
                        //                        System.out.println("Sended msg:" + msg);
                        Thread.sleep(1000 / QPS);//每个Producer发送消息的20qps
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            } catch (RemoteServiceInitFailedException e1) {
                e1.printStackTrace();
            }
        }
    }

}
