package com.dianping.swallow.example.producer;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

/**
 * @rundemo_name 生产者例子(异步)
 */
public class AsyncProducerExample {

    public static void main(String[] args) throws Exception {
        ProducerConfig config = new ProducerConfig();
        //        config.setMode(ProducerMode.ASYNC_MODE);//默认就是异步(ASYNC_MODE)模式
        Producer p = ProducerFactoryImpl.getInstance().createProducer(Destination.topic("example"), config);
        for (int i = 0; i < 10; i++) {
            String msg = AsyncProducerExample.class.getSimpleName() + ": 消息-" + i;
            p.sendMessage(msg);
            System.out.println("Sended msg:" + msg);
            Thread.sleep(500);
        }
    }

}
