package com.dianping.swallow.broker.service;

import java.util.Map;

import com.dianping.swallow.broker.service.impl.ConsumerBroker;

public interface ConsumerHolder {

    Map<String, ConsumerBroker> getConsumerBrokerMap();

    void start();

    void close();

}
