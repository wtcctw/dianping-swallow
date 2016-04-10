package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;

import java.util.NavigableMap;
import java.util.Set;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月20日 下午5:32:22
 */
public class ConsumerIdStatisData extends AbstractStatisable<ConsumerIdData> implements MapRetriever {

    private MessageInfoTotalMapStatis sendMessages = new MessageInfoTotalMapStatis();

    private MessageInfoTotalMapStatis ackMessages = new MessageInfoTotalMapStatis();

    @Override
    public void add(Long time, ConsumerIdData added) {

        sendMessages.add(time, added.getSendMessages());
        ackMessages.add(time, added.getAckMessages());

    }

    @Override
    public void doRemoveBefore(Long time) {

        sendMessages.removeBefore(time);
        ackMessages.removeBefore(time);
    }

    @Override
    public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {

        sendMessages.build(qpx, startKey, endKey, intervalCount);
        ackMessages.build(qpx, startKey, endKey, intervalCount);
    }

    @Override
    public void cleanEmpty() {

        sendMessages.cleanEmpty();
        ackMessages.cleanEmpty();
    }

    @Override
    public boolean isEmpty() {

        return !(!sendMessages.isEmpty() || !ackMessages.isEmpty());
    }

    @Override
    protected Statisable<?> getValue(Object key) {

        throw new UnsupportedOperationException("unsupported method");
    }

    public NavigableMap<Long, StatisData> getData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        switch (statisType) {
            case SEND:
                return sendMessages.getData(retrieveType, statisType, startKey, stopKey);
            case ACK:
                return ackMessages.getData(retrieveType, statisType, startKey, stopKey);
            default:
                throw new IllegalStateException("unsupported type:" + statisType);
        }
    }

//    @Override
//    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type) {
//
//        return getDelayAndQps(type, INFINITY, INFINITY);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type, Long startKey, Long stopKey) {
//
//        switch (type) {
//            case SEND:
//                return sendMessages.getDelayAndQps(type, startKey, stopKey);
//            case ACK:
//                return ackMessages.getDelayAndQps(type, startKey, stopKey);
//            default:
//                throw new IllegalStateException("unsupported type:" + type);
//        }
//    }

    @Override
    public Set<String> getKeys(CasKeys keys, StatisType type) {

        if (type == null) {
            return sendMessages.getKeys(keys, null);
        }
        switch (type) {
            case SEND:
                return sendMessages.getKeys(keys, type);
            case ACK:
                return ackMessages.getKeys(keys, type);
            default:
                throw new IllegalStateException("unsupported type:" + type);
        }
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType) {
        return getStatisData(keys, RetrieveType.ALL_SECTION, statisType, null, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType) {
        return getStatisData(keys, retrieveType, statisType, null, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        if (statisType == null) {
            throw new IllegalStateException("unsupported type:" + statisType);
        }

        switch (statisType) {

            case SEND:
                return sendMessages.getStatisData(keys, retrieveType, statisType, startKey, stopKey);
            case ACK:
                return ackMessages.getStatisData(keys, retrieveType, statisType, startKey, stopKey);
            default:
                throw new IllegalStateException("unsupported type:" + statisType);
        }
    }

    @Override
    public Set<String> getKeys(CasKeys keys) {
        return getKeys(keys, null);
    }

    @Override
    public void merge(String key, KeyMergeable merge) {

        checkType(merge);

        ConsumerIdStatisData toMerge = (ConsumerIdStatisData) merge;
        sendMessages.merge(key, toMerge.sendMessages);
        ackMessages.merge(key, toMerge.ackMessages);
    }

    @Override
    public void merge(Mergeable merge) {

        checkType(merge);

        ConsumerIdStatisData toMerge = (ConsumerIdStatisData) merge;


        sendMessages.merge(toMerge.sendMessages);
        ackMessages.merge(toMerge.ackMessages);
    }

    private void checkType(Mergeable merge) {
        if (!(merge instanceof ConsumerIdStatisData)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("clone not support");
    }

}
