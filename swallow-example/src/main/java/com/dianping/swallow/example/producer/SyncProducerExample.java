package com.dianping.swallow.example.producer;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 同步发送者例子
 */
public class SyncProducerExample {

    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.SYNC_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config);
        for (int i = 0; i < 10; i++) {
            String msg = "消息-" + i;
            p.sendMessage(msg);
            System.out.println("Sended msg:" + msg);
            Thread.sleep(500);
        }
    }

}
