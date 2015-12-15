package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.monitor.impl.AbstractMapMergeable;
import com.dianping.swallow.common.internal.monitor.impl.MapMergeableImpl;
import com.dianping.swallow.common.server.monitor.data.DataSpan;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.StatisData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午5:46:28
 */
public class MessageInfoStatis extends AbstractStatisable<MessageInfo> implements Statisable<MessageInfo>, Mergeable {

    protected transient final Logger logger = LoggerFactory.getLogger(getClass());

    private NavigableMap<Long, MessageInfo> col = new ConcurrentSkipListMap<Long, MessageInfo>();

    private NavigableMap<Long, StatisData> statisMap = new ConcurrentSkipListMap<Long, StatisData>();

    @Override
    public synchronized void add(Long key, MessageInfo rawAdded) {

        if (!(rawAdded instanceof MessageInfo)) {
            throw new IllegalArgumentException("not MessageInfo, but " + rawAdded.getClass());
        }
        MessageInfo added;
        try {
            added = (MessageInfo) rawAdded.clone();
            added.setNoneZeroMergeCount(0);
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("[add]", e);
        }

        MessageInfo messageInfo = col.get(key);

        if (messageInfo == null) {
            col.put(key, added);
        } else {
            messageInfo.merge(added);
        }

    }

    @Override
    public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {
        if (startKey >= endKey) {
            logger.warn("[build][startKey >= endKey]" + startKey + "," + endKey);
            return;
        }

        SortedMap<Long, MessageInfo> sub = col.subMap(startKey, true, endKey, true);
        ajustData(sub, startKey, endKey);
        if(endKey - startKey < 5){
            logger.error("too few key to build statisMap");
        }

        buildStatisData(sub, intervalCount);

        // 统计完，删除原始数据，为了方便debug，保留120条数据
        removeBefore(sub.lastKey() - 120, col, "col,build");

    }

    protected void ajustData(SortedMap<Long, MessageInfo> sub, Long startKey, Long endKey) {

        Long lastDelay = 0L, lastTotal = 0L;
        int noneZeroMergeCount = 0;
        for (Long i = startKey; i <= endKey; i++) {

            MessageInfo currentMessageInfo = sub.get(i);

            if (currentMessageInfo == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[insertLackedData]" + i);
                }
                currentMessageInfo = new MessageInfo();
                sub.put(i, currentMessageInfo);
            }

            if (i > startKey) {
                if (currentMessageInfo.getTotal() < lastTotal || currentMessageInfo.getTotalDelay() < lastDelay) {
                    currentMessageInfo.markDirty();
                }
            }

            lastDelay = currentMessageInfo.getTotalDelay();
            lastTotal = currentMessageInfo.getTotal();

            if (currentMessageInfo.getNonZeroMergeCount() > noneZeroMergeCount) {
                noneZeroMergeCount = currentMessageInfo.getNonZeroMergeCount();
            }
        }

        for (MessageInfo messageInfo : sub.values()) {
            if (messageInfo.getNonZeroMergeCount() < noneZeroMergeCount) {
                messageInfo.markDirty();
            }
        }
    }

    @Override
    public void doRemoveBefore(Long key) {

        removeBefore(key, col, "col");
        removeBefore(key, statisMap, "statisMap");
    }

    private void removeBefore(Long key, NavigableMap<Long, ?> map, String desc) {

        SortedMap<Long, ?> toDelete = map.headMap(key);
        for (Long id : toDelete.keySet()) {
            if (logger.isDebugEnabled()) {
                logger.debug("[removeBefore]" + id + "," + key + "," + desc);
            }
            map.remove(id);
        }
    }

    @Override
    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type) {

        return getDelayAndQps(type, INFINITY, INFINITY);
    }

    @Override
    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type, Long startKey, Long stopKey) {
        if (startKey == INFINITY) {
            if (stopKey != Long.MAX_VALUE) {
                try {
                    startKey = statisMap.firstKey();
                } catch (NoSuchElementException e) {
                    return new ConcurrentSkipListMap<Long, StatisData>();
                }
            } else {
                return onePointFromMap(DataSpan.RIGHTMARGIN);
            }
        } else if (startKey == Long.MIN_VALUE && stopKey != INFINITY) {
            return onePointFromMap(stopKey, DataSpan.RIGHTMARGINLESSTHAN);
        }

        if (stopKey == INFINITY) {
            if (startKey != Long.MIN_VALUE) {
                try {
                    stopKey = statisMap.lastKey();
                } catch (NoSuchElementException e) {
                    return new ConcurrentSkipListMap<Long, StatisData>();
                }
            } else {
                return onePointFromMap(DataSpan.LEFTMARGIN);
            }
        } else if (stopKey == Long.MAX_VALUE && startKey != INFINITY) {
            return onePointFromMap(startKey, DataSpan.LEFTMARGINGREATERTHAN);
        }
        return statisMap.subMap(startKey, true, stopKey, true);
    }

    private NavigableMap<Long, StatisData> onePointFromMap(DataSpan dataSpan) {
        NavigableMap<Long, StatisData> result = new ConcurrentSkipListMap<Long, StatisData>();
        if (dataSpan == DataSpan.LEFTMARGIN) {
            for (Map.Entry<Long, StatisData> entry : statisMap.entrySet()) {
                if (isValid(entry.getValue())) {
                    result.put(entry.getKey(), entry.getValue());
                    break;
                }
            }
        } else if (dataSpan == DataSpan.RIGHTMARGIN) {
            Set<Long> keys = statisMap.descendingKeySet();
            for (Long key : keys) {
                StatisData statisData = statisMap.get(key);
                if (isValid(statisData)) {
                    result.put(key, statisData);
                    break;
                }
            }
        } else {
            throw new UnsupportedOperationException("unsupport type");
        }
        return result;
    }

    private NavigableMap<Long, StatisData> onePointFromMap(Long key, DataSpan dataSpan) {
        NavigableMap<Long, StatisData> result = new ConcurrentSkipListMap<Long, StatisData>();
        Long rightKey;

        if (dataSpan == DataSpan.LEFTMARGINGREATERTHAN) {
            rightKey = statisMap.ceilingKey(key);
        } else if (dataSpan == DataSpan.RIGHTMARGINLESSTHAN) {
            rightKey = statisMap.floorKey(key);
        } else {
            throw new UnsupportedOperationException("unsupport type");
        }

        if (rightKey != null) {
            result.put(rightKey, statisMap.get(rightKey));
        }
        return result;
    }

    private boolean isValid(StatisData statisData) {
        long totalCount = statisData.getTotalCount();
        long totalDelay = statisData.getTotalDelay();
        long count = statisData.getCount();
        long delay = statisData.getDelay();
        if ((totalCount <= 0 && totalDelay <= 0) || count > totalCount || delay > totalDelay) {
            return false;
        }
        return true;
    }

    private void insertStatisData(long count, long delayOfSpan, Long startKey, Byte intervalCount) {

        if (delayOfSpan < 0) {
            delayOfSpan = 0;
        }
        if (count < 0) {
            count = 0;
        }

        StatisData lastStatisData;
        long totalDelay;
        long totalCount;
        long delay = 0L;

        if (statisMap.lastEntry() != null) {
            lastStatisData = statisMap.lastEntry().getValue();
            totalCount = lastStatisData.getTotalCount() + count;
            totalDelay = lastStatisData.getTotalDelay() + delayOfSpan;
        } else {
            totalCount = count;
            totalDelay = delayOfSpan;
        }

        if (count > 0) {
            delay = delayOfSpan / count;
        }

        StatisData statisData = new StatisData(delay, totalDelay, count, totalCount, intervalCount);
        statisMap.put(startKey, statisData);
    }

    private void buildStatisData(SortedMap<Long, MessageInfo> rawData, int intervalCount) {

        int step = 0;
        long count = 0;
        long delayOfSpan = 0;
        Long startKey = rawData.firstKey();
        MessageInfo lastMessageInfo = null;
        int realIntervalCount = 0;

        for (Entry<Long, MessageInfo> entry : rawData.entrySet()) {

            Long key = entry.getKey();
            MessageInfo info = entry.getValue();

            if (step != 0) {
                if (isDataLegal(info, lastMessageInfo)) {

                    count += info.getTotal() - lastMessageInfo.getTotal();
                    delayOfSpan += info.getTotalDelay() - lastMessageInfo.getTotalDelay();
                    realIntervalCount++;
                }
            }

            lastMessageInfo = info;

            if (step >= intervalCount) {

                if (realIntervalCount > 0 && realIntervalCount < intervalCount && count > 0) {
                    count = (long) ((double) count / realIntervalCount * intervalCount);
                    delayOfSpan = (long) ((double) delayOfSpan / realIntervalCount * intervalCount);
                }
                insertStatisData(count, delayOfSpan, startKey, (byte) intervalCount);

                step = 1;
                count = 0;
                delayOfSpan = 0;
                startKey = key;
                realIntervalCount = 0;
                continue;
            }
            step++;

        }
    }

    private boolean isDataLegal(MessageInfo info, MessageInfo lastMessageInfo) {

        return !info.isDirty() && !lastMessageInfo.isDirty() && info.getTotal() > 0 && lastMessageInfo.getTotal() > 0;
    }

    @Override
    public boolean isEmpty() {
        for(StatisData statisData : statisMap.values()){
            if(statisData.getQpx(QPX.SECOND) > 0){
                return false;
            }
        }

        for(MessageInfo info : col.values()){
            if(!info.isEmpty()){
                return false;
            }
        }
        return true;

    }

    @Override
    public void cleanEmpty() {
        // nothing need to be done
    }

    @Override
    public String toString() {
        return "[col]" + col + "\n" + "[statis]" + statisMap;

    }

    @Override
    protected Statisable<?> getValue(Object key) {

        throw new UnsupportedOperationException("unsupported operation getValue()");
    }

    @Override
    public void merge(Mergeable merge) {
        if (!(merge instanceof MessageInfoStatis)) {
            throw new IllegalArgumentException("not MessageInfo, but " + merge.getClass());
        }
        MessageInfoStatis messageInfoStatis = (MessageInfoStatis) merge;
        AbstractMapMergeable<Long, MessageInfo> colMapMergeable = new MapMergeableImpl<Long, MessageInfo>();
        colMapMergeable.merge(this.col);
        colMapMergeable.merge(messageInfoStatis.col);
        this.col = colMapMergeable.getToMerge();

        AbstractMapMergeable<Long, StatisData> statisDataMapMergeable = new MapMergeableImpl<Long, StatisData>();
        statisDataMapMergeable.merge(this.statisMap);
        statisDataMapMergeable.merge(messageInfoStatis.statisMap);
        this.statisMap = statisDataMapMergeable.getToMerge();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        throw new CloneNotSupportedException("clone not support");
    }

    protected void setStatisMap(NavigableMap<Long, StatisData> statisMap) {
        this.statisMap = statisMap;
    }
}
