package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Author   mingdongli
 * 15/11/16  下午5:46.
 */
public class StatisData implements Mergeable {

    /*时间段内的的延迟总数*/
    private Long delay = 0L;

    /*从运行开始后的延迟总数*/
    private Long totalDelay = 0L;

    /*时间段内的消息数*/
    private Long count = 0L;

    /*从运行开始后的消息总条数*/
    private Long totalCount = 0L;

    private Long msgSize = 0L;

    private Long totalMsgSize = 0L;

    @JsonIgnore
    private Byte intervalCount = 6;

    public StatisData() {

    }

    public StatisData(Long delay, Long totalDelay, Long count, Long totalCount, Long msgSize, Long totalMsgSize, Byte intervalCount) {
        this.delay = delay;
        this.totalDelay = totalDelay;
        this.count = count;
        this.totalCount = totalCount;
        this.msgSize = msgSize;
        this.totalMsgSize = totalMsgSize;
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

    public Long getMsgSize() {
        return msgSize;
    }

    public Long getTotalMsgSize() {
        return totalMsgSize;
    }

    public Byte getIntervalCount() {
        return intervalCount;
    }

    public Long getQpx(QPX qpx) {
        switch (qpx) {
            case MINUTE:
                return this.count / (intervalCount * AbstractCollector.SEND_INTERVAL) * 60;
            case SECOND:
            default:
                return this.count / (intervalCount * AbstractCollector.SEND_INTERVAL);
        }
    }

    public Long getAvgDelay() {
        if (count <= 0) {
            return 0L;
        }
        return this.delay / count;
    }

    public Long getAvgMsgSize() {
        if (count <= 0) {
            return 0L;
        }
        return this.msgSize / count;
    }

    @Override
    public void merge(Mergeable merge) {
        if (!(merge instanceof StatisData)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }

        StatisData toMerge = (StatisData) merge;

        this.delay += toMerge.getDelay();
        this.msgSize += toMerge.getMsgSize();
        this.count += toMerge.count;

        this.totalMsgSize += toMerge.totalMsgSize;
        this.totalCount += toMerge.totalCount;
        this.totalDelay += toMerge.totalDelay;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new UnsupportedOperationException("clone not support");
    }

    public boolean isDataLegal() {
        return this.totalCount > 0 || this.totalDelay > 0 || this.totalMsgSize > 0;
    }

    @Override
    public String toString() {
        return JsonBinder.getNonEmptyBinder().toJson(this);
    }
}
