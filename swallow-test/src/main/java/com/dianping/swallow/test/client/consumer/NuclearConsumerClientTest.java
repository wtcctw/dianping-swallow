package com.dianping.swallow.test.client.consumer;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.message.BytesMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.commonnuclear.impl.NuclearDestination;
import com.dianping.swallow.consumer.*;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import jodd.datetime.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2015/12/15  下午7:13.
 */
public class NuclearConsumerClientTest {

    private static final Logger logger = LoggerFactory.getLogger(NuclearConsumerClientTest.class);

    public void consume() {
        ConsumerFactory consumerFactory = ConsumerFactoryImpl.getInstance();
        Destination dest = NuclearDestination.topic("NUCLEARMQ:test_for_shanghai1");
        String consumerId = "mtpoiop.test_for_shanghai1.d1";
        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, new ConsumerConfig());
        consumer.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) throws BackoutMessageException {
                if(msg instanceof BytesMessage) {
                    BytesMessage byteMsg = (BytesMessage) msg;
                }
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
