package com.dianping.swallow.web.monitor.jmx.listener;

import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;

import java.util.EventListener;

/**
 * Author   mingdongli
 * 16/2/23  下午6:26.
 */
public interface KafkaEventListener extends EventListener {

    void onKafkaEvent(KafkaEvent event);
}
