package com.dianping.swallow.web.monitor.jmx.event;

import com.dianping.swallow.web.model.event.AlarmRecord;
import com.dianping.swallow.web.model.event.ServerEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author   mingdongli
 * 16/2/2  下午4:46.
 */
public class KafkaEvent extends ServerEvent {

    protected static final Map<String, AlarmRecord> lastAlarms = new ConcurrentHashMap<String, AlarmRecord>();

}
