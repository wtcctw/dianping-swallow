package com.dianping.swallow.broker.service.consumer;

import java.util.Map;

import com.dianping.swallow.broker.service.consumer.impl.ConsumerBroker;

public interface ConsumerHolder {

    Map<String, ConsumerBroker> getConsumerBrokerMap();

    void start();

    void close();

}
