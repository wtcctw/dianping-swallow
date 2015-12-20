package com.dianping.swallow.consumernuclear.impl;

import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.commonnuclear.impl.NuclearDestination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author qi.yin
 *         2015/12/15  上午11:21.
 */
public class NuclearConsumerFactory implements ConsumerFactory {

    @Override
    public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {
        Consumer consumer = new NuclearConsumer(dest, consumerId, config);
        return consumer;
    }

    @Override
    public Consumer createConsumer(Destination dest, ConsumerConfig config) {
        throw new UnsupportedOperationException("[createConsumer] NuclearMQ unsupported this operation.");
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId) {
        Consumer consumer = createConsumer(dest, consumerId, new ConsumerConfig());
        return consumer;
    }

    @Override
    public Consumer createConsumer(Destination dest) {
        throw new UnsupportedOperationException("[createConsumer] NuclearMQ unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getTopicAddress(String topic) {
        throw new UnsupportedOperationException("[getTopicAddress] NuclearMQ unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic) {
        throw new UnsupportedOperationException("[getOrDefaultTopicAddress] NuclearMQ unsupported this operation.");
    }

    @Override
    public boolean isSupported(Destination dest) {
        return NuclearDestination.supportedDestination(dest);
    }
}
