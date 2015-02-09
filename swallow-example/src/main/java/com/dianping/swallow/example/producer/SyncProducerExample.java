package com.dianping.swallow.example.producer;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(同步)
 */
public class SyncProducerExample {

    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config);
        for (int i = 0; i < 1000; i++) {
            String msg = SyncProducerExample.class.getSimpleName() + "(" + (new Date()) + "): 消息-" + i;
            p.sendMessage(msg);
            System.out.println("Sended msg:" + msg);
            TimeUnit.SECONDS.sleep(5);
        }
    }

}
