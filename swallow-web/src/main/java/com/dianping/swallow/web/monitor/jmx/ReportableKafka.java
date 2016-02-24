package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.Event;

/**
 * Author   mingdongli
 * 16/2/19  下午4:44.
 */
public interface ReportableKafka extends KafkaJmx, EventReporter {

    boolean isReport(Event event);
}
