package com.dianping.swallow.consumer.impl;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qi.yin
 *         2015/12/15  下午6:21.
 */
public class ConsumerFactoryImplTest {

    @Test
    public void createConsumerTest() {
        ConsumerFactory consumerFactory = ConsumerFactoryImpl.getInstance();

        Destination dest = Destination.topic("LoadTestTopic-0");
        String consumerId = "my_cid";
        final Consumer consumer = consumerFactory.createConsumer(dest, consumerId, new ConsumerConfig());

        consumer.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) throws BackoutMessageException {
                Assert.assertNotNull(msg);
            }
        });

        consumer.start();
        Assert.assertFalse(consumer.isClosed());

        consumer.close();

        Assert.assertTrue(consumer.isClosed());
    }

}
