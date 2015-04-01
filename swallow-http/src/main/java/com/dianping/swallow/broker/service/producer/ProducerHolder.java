package com.dianping.swallow.broker.service.producer;

import java.util.Map;

import com.dianping.swallow.producer.Producer;

public interface ProducerHolder {

    Producer getProducer(String topic);

    Map<String, Producer> getProducerMap();
}
