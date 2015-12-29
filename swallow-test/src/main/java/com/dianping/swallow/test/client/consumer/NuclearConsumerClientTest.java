package com.dianping.swallow.test.client.consumer;

import com.dianping.swallow.common.message.BytesMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.*;
import com.dianping.swallow.client.nuclear.consumer.impl.NuclearDestination;
import com.dianping.swallow.client.nuclear.consumer.impl.NuclearConsumerConfig;
import com.dianping.swallow.client.nuclear.consumer.impl.NuclearConsumerFactory;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author qi.yin
 *         2015/12/15  下午7:13.
 */
public class NuclearConsumerClientTest {

    private static final Logger logger = LogManager.getLogger(NuclearConsumerClientTest.class);

    public void consume() {
        ConsumerFactory consumerFactory = new NuclearConsumerFactory("swallow-test", true);
        Destination dest = NuclearDestination.topic("test_for_shanghai1");
        String consumerId = "com.dianping.swallow.swallow-test.test_for_shanghai1.d0";
        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, new NuclearConsumerConfig(true));
        consumer.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) throws BackoutMessageException {
                BytesMessage byteMsg = (BytesMessage) msg;
                byte[] content = byteMsg.getBytesContent();
                long messageId = byteMsg.getMessageId();
            }
        });

        consumer.start();
    }

    public static void main(String[] args) {
        final NuclearConsumerClientTest clientTest = new NuclearConsumerClientTest();

        for (int i = 0; i < 10; i++) {

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    clientTest.consume();
                }
            });
            thread.setDaemon(false);
            thread.setName("ConsumerClient-" + i);
            thread.start();
        }
    }


}
