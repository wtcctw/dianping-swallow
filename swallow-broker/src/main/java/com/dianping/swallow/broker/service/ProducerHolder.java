package com.dianping.swallow.broker.service;

import com.dianping.swallow.producer.Producer;

public interface ProducerHolder {

    Producer getProducer(String topic);
}
