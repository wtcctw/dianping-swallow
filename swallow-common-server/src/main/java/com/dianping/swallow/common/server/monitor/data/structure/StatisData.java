package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Author   mingdongli
 * 15/11/16  下午5:46.
 */
public class StatisData implements Mergeable {

    private Long delay = 0L;

    private Long totalDelay = 0L;

    private Long count = 0L;

    private Long totalCount = 0L;

    @JsonIgnore
    private Byte intervalCount = 6;

    public StatisData(){

    }

    public StatisData(Long delay, Long totalDelay, Long count, Long totalCount, Byte intervalCount) {
        this.delay = delay;
        this.totalDelay = totalDelay;
        this.count = count;
        this.totalCount = totalCount;
        this.intervalCount = intervalCount;
    }

    public Long getDelay() {
        return delay;
    }

    public Long getTotalDelay() {
        return totalDelay;
    }

    public Long getCount() {
        return count;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public Long getQpx(QPX qpx) {
        if(intervalCount <= 0){
            throw new RuntimeException("intervalCount should be positive");
        }
        if (qpx == QPX.MINUTE) {
            return this.count / (intervalCount * AbstractCollector.SEND_INTERVAL) * 60;
        } else if (qpx == QPX.SECOND) {
            return this.count / (intervalCount * AbstractCollector.SEND_INTERVAL);
        } else {
            throw new UnsupportedOperationException("unsupported QPX type");
        }
    }

    @Override
    public void merge(Mergeable merge) {
        if (!(merge instanceof StatisData)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }

        StatisData toMerge = (StatisData) merge;
        Long mergeCount = this.count + toMerge.count;

        if (mergeCount <= 0) {
            this.delay = 0L;
        } else {
            this.delay = (this.delay * this.count + toMerge.delay * toMerge.count) / mergeCount;
        }
        this.count = mergeCount;

        this.totalCount += toMerge.totalCount;
        this.totalDelay += toMerge.totalDelay;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("clone not support");
    }

    @Override
    public String toString() {
        return "StatisData{" +
                "delay=" + delay +
                ", totalDelay=" + totalDelay +
                ", count=" + count +
                ", totalCount=" + totalCount +
                '}';
    }
}
