package com.dianping.swallow.web.monitor.jmx.listener;

import com.dianping.swallow.web.monitor.jmx.event.BrokerKafkaEvent;

import java.util.EventListener;

/**
 * Author   mingdongli
 * 16/2/23  下午6:26.
 */
public interface BrokerKafkaEventListener extends EventListener {

    void onBrokerKafkaEvent(BrokerKafkaEvent event);
}
