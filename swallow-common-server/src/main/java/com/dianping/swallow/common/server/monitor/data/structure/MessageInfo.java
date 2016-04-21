package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.TimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午5:43:38
 */
public class MessageInfo extends AbstractTotalable implements Mergeable, Serializable {

    private static final long serialVersionUID = 1L;

    private AtomicLong totalDelay = new AtomicLong();

    private AtomicLong total = new AtomicLong();

    private AtomicLong totalMsgSize = new AtomicLong();

    private volatile boolean isDirty = false;
    private AtomicInteger noneZeroMergeCount = new AtomicInteger();


    public MessageInfo() {
        this(true);
    }

    /**
     * 是否只真实的数据，或者是插值插入的数据
     *
     * @param isReal
     */
    public MessageInfo(boolean isReal) {
        if (!isReal) {
            noneZeroMergeCount.set(-1);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MessageInfo)) {
            return false;
        }
        MessageInfo cmp = (MessageInfo) obj;

        return cmp.totalDelay.get() == totalDelay.get() && cmp.total.get() == total.get() &&
                cmp.totalMsgSize.get() == totalMsgSize.get() && cmp.isDirty == isDirty;
    }

    @Override
    public int hashCode() {
        return (int) (totalDelay.get() ^ total.get() ^ totalMsgSize.get());
    }

    public void addMessage(long messageId, long msgSize, long startTime, long endTime) {
        total.incrementAndGet();
        totalMsgSize.addAndGet(msgSize);
        if (endTime < startTime) {
            throw new TimeException("start > end", startTime, endTime);
        }
        totalDelay.addAndGet(endTime - startTime);
    }

    public long getTotalMsgSize() {
        return totalMsgSize.get();
    }

    public long getTotalDelay() {
        return totalDelay.get();
    }

    public long getTotal() {
        return total.get();
    }

    @Override
    public void merge(Mergeable merge) {

        if (!(merge instanceof MessageInfo)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }

        MessageInfo toMerge = (MessageInfo) merge;
        if (toMerge.getTotal() > 0 || toMerge.getTotalDelay() > 0 || toMerge.getTotalMsgSize() > 0L) {
            noneZeroMergeCount.incrementAndGet();
        }
        total.addAndGet(toMerge.total.get());
        totalDelay.addAndGet(toMerge.totalDelay.get());
        totalMsgSize.addAndGet(toMerge.totalMsgSize.get());
    }

    @Override
    public String toString() {
        return JsonBinder.getNonEmptyBinder().toJson(this);
    }

    /*不足以判断是否为空，因为数据是累加的*/
    @JsonIgnore
    public boolean isEmpty() {
        if (total.get() > 0 || totalDelay.get() > 0 || totalMsgSize.get() > 0) {
            return false;
        }
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        MessageInfo info = (MessageInfo) super.clone();
        info.total = new AtomicLong(total.get());
        info.totalDelay = new AtomicLong(totalDelay.get());
        info.totalMsgSize = new AtomicLong(totalMsgSize.get());
        info.noneZeroMergeCount = new AtomicInteger(noneZeroMergeCount.get());
        return info;
    }


    /**
     * 标记此数据为脏数据
     */
    public void markDirty() {
        isDirty = true;
    }

    @JsonIgnore
    public boolean isDirty() {
        return isDirty;
    }

    @JsonIgnore
    public int getNonZeroMergeCount() {
        return noneZeroMergeCount.get();
    }

    public void setNoneZeroMergeCount(int noneZeroMergeCount) {

        this.noneZeroMergeCount.set(noneZeroMergeCount);
    }

}
