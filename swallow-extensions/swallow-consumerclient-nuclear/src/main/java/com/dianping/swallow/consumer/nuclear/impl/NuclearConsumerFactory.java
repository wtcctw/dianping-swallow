package com.dianping.swallow.consumer.nuclear.impl;

import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.AbstractConsumerFactory;
import com.dianping.swallow.consumer.nuclear.common.impl.NuclearDestination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;

/**
 * @author qi.yin
 *         2015/12/15  上午11:21.
 */
public class NuclearConsumerFactory extends AbstractConsumerFactory implements ConsumerFactory {

    @Override
    public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {
        Consumer consumer = new NuclearConsumer(dest, consumerId, config);
        return consumer;
    }

    @Override
    public Consumer createConsumer(Destination dest, ConsumerConfig config) {
        throw new UnsupportedOperationException("[createConsumer] unsupported this operation.");
    }


    @Override
    public Consumer createConsumer(Destination dest) {
        throw new UnsupportedOperationException("[createConsumer] unsupported this operation.");
    }


    @Override
    public boolean isSupported(Destination dest) {
        return NuclearDestination.supportedDestination(dest);
    }
}
