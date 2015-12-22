package com.dianping.test.swallow.log4j2;


import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

public class SyncProducerRunner {

    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();

        config.setMode(ProducerMode.SYNC_MODE);//默认就是异步(ASYNC_MODE)模式
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("product-kafka-test"), config);
        for (int i = 0; i < 10; i++) {
            String msg = SyncProducerRunner.class.getSimpleName() + ": 消息-" + i;
            p.sendMessage(msg);
            System.out.println("Sended msg:" + msg);
            Thread.sleep(500);
        }
    }

}
