package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Transient;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author qiyin
 *         <p/>
 *         2015年7月31日 下午3:56:58
 */
public abstract class ProducerStatsData extends StatsData {

    public ProducerStatsData() {
        eventType = EventType.PRODUCER;
    }

    private long qps;

    @Transient
    private long qpsTotal;

    private long delay;

    public long getQps() {
        return qps;
    }

    public void setQps(long qps) {
        this.qps = qps;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    @Override
    public String toString() {
        return "ProducerStatsData [qps=" + qps + ", delay=" + delay + "]" + super.toString();
    }

    @JsonIgnore
    public long getQpsTotal() {
        return qpsTotal;
    }

    @JsonIgnore
    public void setQpsTotal(long qpsTotal) {
        this.qpsTotal = qpsTotal;
    }

    public boolean checkQpsPeak(long expectQps) {
        return checkQpsPeak(this.getQps(), expectQps, StatisType.SENDQPS_PEAK);
    }

    public boolean checkQpsValley(long expectQps, int currentCount) {
        return checkQpsValley(this.getQps(), expectQps, currentCount, StatisType.SENDQPS_VALLEY);
    }

    public boolean checkQpsValley(long expectQps) {
        return checkQpsValley(this.getQps(), expectQps, StatisType.SENDQPS_VALLEY);
    }

}
