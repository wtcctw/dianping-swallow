package com.dianping.swallow.consumer.impl;

import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.common.internal.util.ServiceLoaderUtil;
import com.dianping.swallow.consumer.AbstractConsumerFactory;
import org.apache.log4j.Logger;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;

public final class ConsumerFactoryImpl extends AbstractConsumerFactory {

    private Logger logger = Logger.getLogger(getClass());

    private static ConsumerFactoryImpl instance = new ConsumerFactoryImpl();

    private ConsumerFactory consumerFactory;

    static {
        SwallowHelper.initialize();
    }

    private ConsumerFactoryImpl() {
    }

    public static ConsumerFactory getInstance() {
        return instance;
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId, ConsumerConfig config) {
        consumerFactory = findConsumerFactory(dest);

        if (logger.isInfoEnabled()) {
            logger.info("[findConsumerFactory] destination: " + dest.toString() + " consumerFactory: " + consumerFactory.getClass().getSimpleName());
        }

        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, config);
        return consumer;
    }

    @Override
    public List<InetSocketAddress> getTopicAddress(String topic) {
        return consumerFactory.getTopicAddress(topic);
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic) {
        return consumerFactory.getOrDefaultTopicAddress(topic);
    }

    private ConsumerFactory findConsumerFactory(Destination dest) {

        List<ConsumerFactory> factories = ServiceLoaderUtil.getServices(ConsumerFactory.class);

        if (factories != null && !factories.isEmpty()) {

            for (ConsumerFactory factory : factories) {
                if (factory.isSupported(dest)) {

                    return factory;
                }
            }
        }

        return DefaultConsumerFactory.getInstance();
    }

}
