package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisEvent;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * @author qiyin
 *         <p/>
 *         2015年7月31日 下午3:57:09
 */
public abstract class StatsData {

    @Transient
    private static final int VALLEY_COUNT = 3;

    @Id
    private String id;

    @Indexed(name = "IX_TIMEKEY", direction = IndexDirection.ASCENDING)
    private long timeKey;

    @Transient
    protected EventReporter eventReporter;

    @Transient
    protected EventFactory eventFactory;

    @Transient
    protected EventType eventType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(long timeKey) {
        this.timeKey = timeKey;
    }

    public void setEventReporter(EventReporter eventReporter) {
        this.eventReporter = eventReporter;
    }

    public void setEventFactory(EventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public String toString() {
        return "StatsData [id=" + id + ", timeKey=" + timeKey + "]";
    }

    protected void report(long currentValue, long expectedValue, StatisType statisType) {
        eventReporter.report(createEvent().setCurrentValue(currentValue).setExpectedValue(expectedValue)
                .setStatisType(statisType).setCreateTime(new Date()).setEventType(eventType));
    }

    public abstract StatisEvent createEvent();

    protected boolean checkQpsPeak(long qps, long expectQps, StatisType statisType) {
        if (qps != 0L) {
            if (qps > expectQps) {
                report(qps, expectQps, statisType);
                return false;
            }
        }
        return true;
    }

    protected boolean checkQpsValley(long qps, long expectQps, int currentCount, StatisType statisType) {
        if (qps < expectQps) {
            if (currentCount >= VALLEY_COUNT) {
                report(qps, expectQps, statisType);
            }
            return false;
        }
        return true;
    }

    protected boolean checkQpsValley(long qps, long expectQps, StatisType statisType) {
        if (qps != 0L) {
            if (qps < expectQps) {
                report(qps, expectQps, statisType);
                return false;
            }
        }
        return true;
    }

    protected boolean checkQpsFlu(long qps, long baseQps, long preQps, int flu, StatisType statisType) {
        if (qps == 0L || preQps == 0L) {
            return true;
        }

        if (qps > baseQps || preQps > baseQps) {

            if ((qps >= preQps && (qps / preQps > flu)) || (qps < preQps && (preQps / qps > flu))) {

                report(qps, preQps, statisType);
                return false;

            }
        }
        return true;
    }

    protected boolean checkDelay(long delay, long expectDelay, StatisType statisType) {
        delay = delay / 1000;
        if (delay != 0L && expectDelay != 0L) {
            if ((delay) > expectDelay) {
                report(delay, expectDelay, statisType);
                return false;
            }
        }
        return true;
    }

    protected boolean checkMsgSize(long msgSize, long expectMsgSize, StatisType statisType) {
        msgSize = msgSize / 1000;
        if (msgSize != 0L && expectMsgSize != 0L) {
            if ((msgSize) > expectMsgSize) {
                report(msgSize, expectMsgSize, statisType);
                return false;
            }
        }
        return true;
    }

    protected boolean checkAccu(long accu, long expectAccu, StatisType statisType) {
        if (accu != 0L && expectAccu != 0L) {
            if (accu > expectAccu) {
                report(accu, expectAccu, statisType);
                return false;
            }
        }
        return true;
    }

}
