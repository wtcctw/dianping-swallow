package com.dianping.swallow.consumer.impl;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerFactory;
import org.junit.Test;

/**
 * @author qi.yin
 *         2015/12/15  下午6:21.
 */
public class ConsumerFactoryImplTest {


    @Test
    public void createConsumerTest() {
        ConsumerFactory consumerFactory = ConsumerFactoryImpl.getInstance();
        Destination dest = Destination.topic("example");
        String consumerId = "example";
        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, null);
    }

}
