package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.monitor.impl.AbstractMapMergeable;
import com.dianping.swallow.common.internal.monitor.impl.MapMergeableImpl;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午5:46:28
 */
public class MessageInfoStatis extends AbstractStatisable<MessageInfo> implements Statisable<MessageInfo>, Mergeable {

    protected transient final Logger logger = LogManager.getLogger(getClass());

    private NavigableMap<Long, MessageInfo> col = new ConcurrentSkipListMap<Long, MessageInfo>();

    private NavigableMap<Long, StatisData> statis = new ConcurrentSkipListMap<Long, StatisData>();

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
        if (endKey - startKey < 5) {
            logger.error("too few key to build statisMap");
        }

        buildStatisData(sub, intervalCount);

        // 统计完，删除原始数据，为了方便debug，保留120条数据
        removeBefore(sub.lastKey() - 120, col, "col,build");

    }

    protected void ajustData(SortedMap<Long, MessageInfo> sub, Long startKey, Long endKey) {

        Long lastDelay = 0L, lastTotal = 0L, lastSize = 0L;
        int noneZeroMergeCount = 0;
        for (Long i = startKey; i <= endKey; i++) {

            MessageInfo messageInfo = sub.get(i);

            if (messageInfo == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug("[insertLackedData]" + i);
                }
                messageInfo = new MessageInfo();
                sub.put(i, messageInfo);
            }

            if (i > startKey) {
                if (messageInfo.getTotal() < lastTotal || messageInfo.getTotalDelay() < lastDelay
                        || messageInfo.getTotalMsgSize() < lastSize) {
                    messageInfo.markDirty();
                }
            }

            lastDelay = messageInfo.getTotalDelay();
            lastTotal = messageInfo.getTotal();
            lastSize = messageInfo.getTotalMsgSize();
            if (messageInfo.getNonZeroMergeCount() > noneZeroMergeCount) {
                noneZeroMergeCount = messageInfo.getNonZeroMergeCount();
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
        removeBefore(key, statis, "statis");
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
    public NavigableMap<Long, StatisData> getData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        if (statis.isEmpty()) {
            return null;
        }

        if (retrieveType.isPoint()) {
            return getPointData(retrieveType, statisType, startKey, stopKey);
        } else {
            return getSectionData(retrieveType, statisType, startKey, stopKey);
        }
    }

    private NavigableMap<Long, StatisData> getPointData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        Long key = null;
        NavigableMap<Long, StatisData> result = new ConcurrentSkipListMap<Long, StatisData>();

        switch (retrieveType) {
            case MIN_POINT:
                key = statis.firstKey();
                break;
            case MAX_POINT:
                key = statis.lastKey();
                break;
            case LESS_POINT:
                if (stopKey == null) {
                    throw new IllegalArgumentException("stopKey is null");
                }
                key = statis.floorKey(stopKey);
                break;
            case MORE_POINT:
                if (startKey == null) {
                    throw new IllegalArgumentException("startKey is null");
                }
                key = statis.ceilingKey(startKey);
                break;
        }

        if (key != null) {
            result.put(key, statis.get(key));
        }
        return result;
    }

    private NavigableMap<Long, StatisData> getSectionData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        switch (retrieveType) {
            case GENERAL_SECTION:
                if (startKey == null || stopKey == null) {
                    throw new IllegalArgumentException("startKey is null&&stopKey is null");
                }
                break;
            case ALL_SECTION:
                startKey = statis.firstKey();
                stopKey = statis.lastKey();
                break;
        }

        return statis.subMap(startKey, true, stopKey, true);
    }

    private void buildStatisData(SortedMap<Long, MessageInfo> rawData, int intervalCount) {

        int step = 0;
        long count = 0, delay = 0, msgSize = 0;
        Long startKey = rawData.firstKey();
        MessageInfo lastMessageInfo = null;
        int realIntervalCount = 0;

        for (Entry<Long, MessageInfo> entry : rawData.entrySet()) {

            Long key = entry.getKey();
            MessageInfo info = entry.getValue();

            if (step != 0) {
                if (isDataLegal(info, lastMessageInfo)) {

                    count += info.getTotal() - lastMessageInfo.getTotal();
                    delay += info.getTotalDelay() - lastMessageInfo.getTotalDelay();
                    msgSize += info.getTotalMsgSize() - lastMessageInfo.getTotalMsgSize();
                    realIntervalCount++;
                }
            }

            lastMessageInfo = info;

            if (step >= intervalCount) {

                if (realIntervalCount > 0 && realIntervalCount < intervalCount && count > 0) {
                    count = (long) ((double) count / realIntervalCount * intervalCount);
                    delay = (long) ((double) delay / realIntervalCount * intervalCount);
                    msgSize = (long) ((double) msgSize / realIntervalCount * intervalCount);
                }
                insertStatisData(count, delay, msgSize, startKey, (byte) intervalCount);

                step = 1;
                count = 0;
                delay = 0;
                msgSize = 0;
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

    private void insertStatisData(long count, long delay, long msgSize, Long startKey, Byte intervalCount) {
        StatisData lastStatisData;
        long totalDelay = delay;
        long totalCount = count;
        long totalMsgSize = msgSize;

        if (statis.lastEntry() != null) {
            lastStatisData = statis.lastEntry().getValue();
            totalCount += lastStatisData.getTotalCount();
            totalDelay += lastStatisData.getTotalDelay();
            totalMsgSize += lastStatisData.getTotalMsgSize();
        }

        StatisData statisData = new StatisData(delay, totalDelay, count, totalCount, msgSize, totalMsgSize, intervalCount);
        statis.put(startKey, statisData);
    }

    @Override
    public boolean isEmpty() {
        for (StatisData statisData : statis.values()) {
            if (statisData.getCount() > 0 || statisData.getDelay() > 0 || statisData.getMsgSize() > 0) {
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
        return "[col]" + col + "\n" + "[statis]" + statis;

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
        MessageInfoStatis mergeable = (MessageInfoStatis) merge;
        AbstractMapMergeable<Long, MessageInfo> colMap = new MapMergeableImpl<Long, MessageInfo>();
        colMap.merge(this.col);
        colMap.merge(mergeable.col);
        this.col = colMap.getToMerge();

        AbstractMapMergeable<Long, StatisData> statisMap = new MapMergeableImpl<Long, StatisData>();
        statisMap.merge(this.statis);
        statisMap.merge(mergeable.statis);
        this.statis = statisMap.getToMerge();

    }

    @Override
    public Object clone() throws CloneNotSupportedException {

        throw new CloneNotSupportedException("clone not support");
    }

    protected void setStatisMap(NavigableMap<Long, StatisData> statis) {
        this.statis = statis;
    }
}
