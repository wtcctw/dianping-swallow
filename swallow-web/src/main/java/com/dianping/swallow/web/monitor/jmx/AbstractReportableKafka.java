package com.dianping.swallow.web.monitor.jmx;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.monitor.jmx.event.KafkaEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Author   mingdongli
 * 16/2/2  下午3:42.
 */
public abstract class AbstractReportableKafka extends AbstractLifecycle implements ReportableKafka, AlarmerLifecycle {

    protected final Logger logger = LogManager.getLogger(getClass());

    @Autowired
    @Qualifier("eventReporter")
    private EventReporter eventReporter;

    @Autowired
    protected EventFactory eventFactory;

    @Override
    public void report(Event event) {
        if (isReport(event)) {
            eventReporter.report(event);
        }
    }

    @Override
    public boolean isReport(Event event) {
        return true;
    }

    abstract protected int getInterval();

    abstract protected int getDelay();

    abstract protected KafkaEvent createEvent();

}
