package com.dianping.swallow.consumer.internal;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.MessageListener;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mingdongli
 *         15/10/29 上午10:30
 */
public class ConsumerImplTest {

    private static Logger logger = LogManager.getLogger(ConsumerImplTest.class);

    public static void main(String[] args) {
        ConsumerConfig config = new ConsumerConfig();
        //以下根据自己情况而定，默认是不需要配的
        config.setThreadPoolSize(1);
        Consumer c = ConsumerFactoryImpl.getInstance().createConsumer(Destination.topic("example"), "myId", config);
        c.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) {
                logger.info(msg.getContent());
            }
        });
        c.start();  //(5)
    }

}