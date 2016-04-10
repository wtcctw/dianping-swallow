package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.monitor.impl.AbstractMapMergeable;
import com.dianping.swallow.common.internal.monitor.impl.MapMergeableImpl;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 服务器相关，存储server对应的监控数据
 *
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午4:45:31
 */
public abstract class AbstractAllData<M extends Mergeable, T extends TotalMap<M>, S extends AbstractTotalMapStatisable<M, T>, V extends MonitorData>
        extends AbstractStatisable<V> implements StatisRetriever {

    protected Map<String, S> servers = new ConcurrentHashMap<String, S>();

    protected final Set<StatisType> supportedTypes = new HashSet<StatisType>();

    public AbstractAllData(StatisType... types) {

        for (StatisType type : types) {
            supportedTypes.add(type);
        }
    }


    @Override
    public Set<String> getTopics(boolean includeTotal) {
        Set<String> topics = new HashSet<String>();
        for (S s : servers.values()) {
            Set<String> topicSet = s.keySet(includeTotal);
            if (topicSet != null) {
                topics.addAll(topicSet);
            }
        }
        return topics;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(Long time, V added) {

        String serverIp = added.getSwallowServerIp();

        S statis = MapUtil.getOrCreate(servers, serverIp, getStatisClass());

        statis.add(time, (T) added.getServerData());
    }

    protected abstract Class<? extends S> getStatisClass();

    @Override
    public void doRemoveBefore(Long time) {

        for (S s : servers.values()) {
            s.removeBefore(time);
        }
    }


    @Override
    protected Statisable<?> getValue(Object key) {
        return servers.get(key);
    }

    @Override
    public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {

        for (Entry<String, S> entry : servers.entrySet()) {

            String key = entry.getKey();
            S value = entry.getValue();
            if (logger.isDebugEnabled()) {
                logger.debug("[build][" + startKey + "," + endKey + "," + intervalCount + "]" + key + "," + value);
            }
            value.build(qpx, startKey, endKey, intervalCount);
        }

    }

    @Override
    public void cleanEmpty() {
        for (Entry<String, S> entry : servers.entrySet()) {

            String key = entry.getKey();
            S value = entry.getValue();

            value.cleanEmpty();
            if (!isTotalKey(key) && value.isEmpty()) {
                if (logger.isInfoEnabled()) {
                    logger.info("[clean]" + key);
                }
                servers.remove(key);
            }
        }
    }

    @Override
    public boolean isEmpty() {

        for (S s : servers.values()) {
            if (!s.isEmpty()) {
                return false;
            }
        }
        return true;
    }

//    @Override
//    public NavigableMap<Long, Long> getDelay(StatisType type) {
//
//        return convertStatisData(getDelayAndQps(type));
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getQpx(StatisType type) {
//
//        return getDelayAndQps(type);
//    }

    @Override
    public NavigableMap<Long, StatisData> getData(RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        return getStatisDataForTopic(MonitorData.TOTAL_KEY, retrieveType, statisType, startKey, stopKey);
    }
//        @Override
//    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type) {
//        return getDelayAndQps(type, INFINITY, INFINITY);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getDelayAndQps(StatisType type, Long startKey, Long stopKey) {
//        return getStatisDataForTopic(MonitorData.TOTAL_KEY, type, startKey, stopKey);
//    }

    @Override
    public NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, StatisType statisType) {
        return getStatisDataForTopic(topic, RetrieveType.ALL_SECTION, statisType, null, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, RetrieveType retrieveType, StatisType statisType) {

        return getStatisDataForTopic(topic, retrieveType, statisType, null, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {

        checkSupported(statisType);

        AbstractMapMergeable<Long, StatisData> mapMergeableImpl = new MapMergeableImpl<Long, StatisData>();
        Statisable<?> statis;
        for (S s : servers.values()) {
            statis = s.getValue(topic);
            if (statis != null) {
                NavigableMap<Long, StatisData> delay = statis.getData(retrieveType, statisType, startKey, stopKey);
                mapMergeableImpl.merge(delay);
            }
        }

        NavigableMap<Long, StatisData> result = mapMergeableImpl.getToMerge();
        return result.isEmpty() ? null : result;
    }

    public Set<String> getKeys(CasKeys keys, StatisType type) {

        if (!keys.hasNextKey()) {
            return new HashSet<String>(servers.keySet());
        }

        String key = keys.getNextKey();
        if (MonitorData.TOTAL_KEY.equals(key)) {
            Set<String> result = new HashSet<String>();
            for (S s : servers.values()) {
                Set<String> items = s.getKeys(keys, type);
                if (items != null) {
                    result.addAll(items);
                }
                keys.reset();
            }
            return result;
        }
        S server = servers.get(key);

        if (server == null) {
            throw new UnfoundKeyException(key);
        }

        return server.getKeys(keys, type);
    }

    public Set<String> getKeys(CasKeys keys) {

        return getKeys(keys, null);
    }

    private void checkSupported(StatisType type) {
        if (!supportedTypes.contains(type)) {
            throw new IllegalArgumentException("unsupported type:" + type + ", class:" + getClass());
        }
    }

    @Override
    public Map<String, NavigableMap<Long, StatisData>> getQpxForServers(StatisType type) {

        checkSupported(type);
        HashMap<String, NavigableMap<Long, StatisData>> result = new HashMap<String, NavigableMap<Long, StatisData>>();
        for (Entry<String, S> entry : servers.entrySet()) {

            String serverIp = entry.getKey();
            S pssd = entry.getValue();
            result.put(serverIp, pssd.getData(RetrieveType.ALL_SECTION, type, null, null));
        }

        return result;
    }

    public String toString(String key) {

        return JsonBinder.getNonEmptyBinder().toPrettyJson(servers.get(key));
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        if (!keys.hasNextKey()) {
            throw new UnfoundKeyException(keys.toString());
        }

        String key = keys.getNextKey();
        S server;
        if (MonitorData.TOTAL_KEY.equals(key)) {
            AbstractMapMergeable<Long, StatisData> mapMergeableImpl = new MapMergeableImpl<Long, StatisData>();
            for (S s : servers.values()) {
                NavigableMap<Long, StatisData> value = s.getStatisData(keys, retrieveType, statisType, startKey, stopKey);
                mapMergeableImpl.merge(value);
                keys.reset();
            }
            return mapMergeableImpl.getToMerge();
        } else {
            server = servers.get(key);
        }

        if (server == null) {
            throw new UnfoundKeyException(key);
        }

        if (!keys.hasNextKey()) {
            return server.getData(retrieveType, statisType, startKey, stopKey);
        }
        return server.getStatisData(keys, retrieveType, statisType, startKey, stopKey);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType) {
        return getStatisData(keys, retrieveType, statisType, null, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType) {
        return getStatisData(keys, RetrieveType.ALL_SECTION, statisType, null, null);
    }


//    @Override
//    public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {
//        return getDelayValue(keys, type, INFINITY, INFINITY);
//    }
//
//    @Override
//    public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type, Long startKey, Long stopKey) {
//        NavigableMap<Long, StatisData> statisData = getStatisData(keys, type, startKey, stopKey);
//        return convertStatisData(statisData);
//    }

//    @Override
//    public NavigableMap<Long, StatisData> getQpsValue(CasKeys keys, StatisType type) {
//        return getQpsValue(keys, type, INFINITY, INFINITY);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getQpsValue(CasKeys keys, StatisType type, Long startKey, Long stopKey) {
//        return getStatisData(keys, type, startKey, stopKey);
//    }
//
//    private NavigableMap<Long, StatisData> getMarginValue(CasKeys keys, StatisType type, DataSpan dataSpan) {
//        if (DataSpan.LEFTMARGIN == dataSpan) {
//            return getStatisData(keys, type, Long.MIN_VALUE, INFINITY);
//        } else if (DataSpan.RIGHTMARGIN == dataSpan) {
//            return getStatisData(keys, type, INFINITY, Long.MAX_VALUE);
//        } else {
//            throw new UnsupportedOperationException("unsupported type");
//        }
//    }

//    @Override
//    public NavigableMap<Long, StatisData> getFirstValue(CasKeys keys, StatisType type) {
//        return getMarginValue(keys, type, DataSpan.LEFTMARGIN);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getLastValue(CasKeys keys, StatisType type) {
//        return getMarginValue(keys, type, DataSpan.RIGHTMARGIN);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getFirstValueGreaterOrEqualThan(CasKeys keys, StatisType type, Long startKey) {
//        return getStatisData(keys, type, startKey, Long.MAX_VALUE);
//    }
//
//    @Override
//    public NavigableMap<Long, StatisData> getLastValueLessOrEqualThan(CasKeys keys, StatisType type, Long stopKey) {
//        return getStatisData(keys, type, Long.MIN_VALUE, stopKey);
//    }

    @Override
    public NavigableMap<Long, StatisData> getMinData(CasKeys keys, StatisType type) {
        return getStatisData(keys, RetrieveType.MIN_POINT, type);
    }

    @Override
    public NavigableMap<Long, StatisData> getMaxData(CasKeys keys, StatisType type) {
        return getStatisData(keys, RetrieveType.MAX_POINT, type);
    }

    @Override
    public NavigableMap<Long, StatisData> getMoreThanData(CasKeys keys, StatisType type, Long startKey) {
        return getStatisData(keys, RetrieveType.MORE_POINT, type, startKey, null);
    }

    @Override
    public NavigableMap<Long, StatisData> getLessThanData(CasKeys keys, StatisType type, Long stopKey) {
        return getStatisData(keys, RetrieveType.LESS_POINT, type, null, stopKey);
    }


    protected abstract S createValue();

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("clone not supported");
    }

//    protected NavigableMap<Long, Long> convertStatisData(NavigableMap<Long, StatisData> map) {
//        if (map == null) {
//            return null;
//        }
//        NavigableMap<Long, Long> resultMap = new ConcurrentSkipListMap<Long, Long>();
//        for (Map.Entry<Long, StatisData> entry : map.entrySet()) {
//            resultMap.put(entry.getKey(), entry.getValue().getDelay());
//        }
//        return resultMap;
//    }

    protected NavigableMap<Long, Long> convertData(NavigableMap<Long, StatisData> datas, StatisFunctionType type) {
        NavigableMap<Long, Long> result = null;
        if (datas != null) {
            result = new ConcurrentSkipListMap<Long, Long>();
            switch (type) {
                case DELAY:
                    for (Entry<Long, StatisData> entry : datas.entrySet()) {
                        result.put(entry.getKey(), entry.getValue().getAvgDelay());
                    }
                    break;
                case QPX:
                    for (Entry<Long, StatisData> entry : datas.entrySet()) {
                        result.put(entry.getKey(), entry.getValue().getQpx(QPX.SECOND));
                    }
                    break;
                case SIZE:
                    for (Entry<Long, StatisData> entry : datas.entrySet()) {
                        result.put(entry.getKey(), entry.getValue().getAvgMsgSize());
                    }
                    break;
            }
        }
        return result;
    }

}
