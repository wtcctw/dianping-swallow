package com.dianping.swallow.example.producer;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(异步)
 */
public class AsyncSeparateProducerExample {

    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();
        config.setMode(ProducerMode.ASYNC_SEPARATELY_MODE);
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config);
        for (int i = 0; i < 1000; i++) {
            String msg = AsyncSeparateProducerExample.class.getSimpleName() + ": 消息-" + i;
            p.sendMessage(msg);
            System.out.println("Sended msg:" + msg);
            Thread.sleep(50);
        }
    }

}
