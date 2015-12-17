package com.dianping.swallow.consumer.impl;

import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.common.internal.util.ServiceLoaderUtil;
import org.apache.log4j.Logger;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.ConsumerConfig;
import com.dianping.swallow.consumer.ConsumerFactory;

public final class ConsumerFactoryImpl implements ConsumerFactory {

    private Logger logger = Logger.getLogger(getClass());

    private static ConsumerFactoryImpl instance = new ConsumerFactoryImpl();

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
        ConsumerFactory consumerFactory = findConsumerFactory(dest);

        if (consumerFactory == null) {

            consumerFactory = DefaultConsumerFactory.getInstance();
            dest = Destination.topic(dest.getName());
        }

        Consumer consumer = consumerFactory.createConsumer(dest, consumerId, config);
        return consumer;
    }

    @Override
    public Consumer createConsumer(Destination dest, String consumerId) {
        return createConsumer(dest, consumerId, null);
    }

    @Override
    public Consumer createConsumer(Destination dest, ConsumerConfig config) {
        return createConsumer(dest, null, config);
    }

    @Override
    public Consumer createConsumer(Destination dest) {
        return createConsumer(dest, null, null);
    }


    /**
     * for unittest
     *
     * @param topic
     * @return
     */
    @Override
    public List<InetSocketAddress> getTopicAddress(String topic) {
        return null;
    }

    @Override
    public List<InetSocketAddress> getOrDefaultTopicAddress(String topic) {
        return null;
    }

    @Override
    public boolean isSupported(Destination dest) {

        throw new UnsupportedOperationException("[isSupported] NuclearMQ unsupported this operation.");
    }

    private ConsumerFactory findConsumerFactory(Destination dest) {

        List<ConsumerFactory> factories = ServiceLoaderUtil.loadServices(ConsumerFactory.class);

        if (factories != null && !factories.isEmpty()) {

            for (ConsumerFactory factory : factories) {
                if (factory.isSupported(dest)) {

                    if (logger.isInfoEnabled()) {
                        logger.info("[findConsumerFactory] find consumerFactory.");
                    }
                    return factory;
                }
            }
        }

        if (logger.isInfoEnabled()) {
            logger.info("[findConsumerFactory] use default consumerFactory.");
        }

        return null;
    }

}
