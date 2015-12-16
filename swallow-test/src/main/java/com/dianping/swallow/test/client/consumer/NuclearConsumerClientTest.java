package com.dianping.swallow.test.client.consumer;

import com.dianping.swallow.common.internal.message.BytesSwallowMessage;
import com.dianping.swallow.common.message.BytesMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.commonnuclear.impl.NuclearDestination;
import com.dianping.swallow.consumer.*;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;
import org.junit.Test;

/**
 * @author qi.yin
 *         2015/12/15  下午7:13.
 */
public class NuclearConsumerClientTest {

    public void createConsumerTest() {
        ConsumerFactory consumerFactory = ConsumerFactoryImpl.getInstance();
        Destination dest = NuclearDestination.topic("NUCLEARMQ:test_for_shanghai1");
        String consumerId = "mtpoiop.test_for_shanghai1.d1";
        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, new ConsumerConfig());
        consumer.setListener(new MessageListener() {
            @Override
            public void onMessage(Message msg) throws BackoutMessageException {
                if(msg instanceof BytesMessage) {
                    BytesMessage byteMsg = (BytesMessage) msg;
                    System.out.println(new String(byteMsg.getBytesContent()));
                }
            }
        });
        consumer.start();
    }

    public static void main(String[] args) {
        NuclearConsumerClientTest test = new NuclearConsumerClientTest();
        test.createConsumerTest();
    }
}
