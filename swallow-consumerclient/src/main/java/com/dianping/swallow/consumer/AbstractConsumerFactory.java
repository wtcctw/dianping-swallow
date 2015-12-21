package com.dianping.swallow.consumer;

import com.dianping.swallow.common.internal.observer.impl.AbstractObservable;
import com.dianping.swallow.common.message.Destination;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author qi.yin
 *         2015/12/21  下午3:43.
 */
public abstract class AbstractConsumerFactory extends AbstractObservable implements ConsumerFactory {

    @Override
    public Consumer createConsumer(Destination dest, String consumerId) {
        return createConsumer(dest, consumerId, new ConsumerConfig());
    }

    @Override
    public Consumer createConsumer(Destination dest, ConsumerConfig config) {
        return createConsumer(dest, null, config);
    }

    @Override
    public Consumer createConsumer(Destination dest) {
        return createConsumer(dest, new ConsumerConfig());
    }

    @Override
    public boolean isSupported(Destination dest) {

        throw new UnsupportedOperationException("[isSupported] unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getTopicAddress(String topic) {
        throw new UnsupportedOperationException("[getTopicAddress] unsupported this operation.");
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic) {
        throw new UnsupportedOperationException("[getOrDefaultTopicAddress] unsupported this operation.");
    }
}
