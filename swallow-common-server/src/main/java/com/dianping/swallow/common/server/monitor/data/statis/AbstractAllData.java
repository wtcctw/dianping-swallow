package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.monitor.impl.MapMergeableImpl;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

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

    @Override
    public NavigableMap<Long, Long> getDelay(StatisType type) {

        checkSupported(type);
        return getDelayForTopic(MonitorData.TOTAL_KEY, type);
    }

    @Override
    public NavigableMap<Long, QpxData> getQpx(StatisType type) {

        checkSupported(type);
        return getQpxForTopic(MonitorData.TOTAL_KEY, type);
    }


    @Override
    public NavigableMap<Long, QpxData> getQpxForTopic(String topic, StatisType type) {

        checkSupported(type);

        MapMergeableImpl<Long, QpxData> mapMergeableImpl = new MapMergeableImpl<Long, QpxData>();
        Statisable<M> statis;
        for (S s : servers.values()) {
            statis = s.getValue(topic);
            if (statis != null) {
                NavigableMap<Long, QpxData> qps = statis.getQpx(type);
                mapMergeableImpl.merge(qps);
            }
        }

        NavigableMap<Long, QpxData> result = mapMergeableImpl.getToMerge();
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
    public NavigableMap<Long, Long> getDelayForTopic(String topic, StatisType type) {

        checkSupported(type);

        MapMergeableImpl<Long, Long> mapMergeableImpl = new MapMergeableImpl<Long, Long>();
        Statisable<?> statis;
        for (S s : servers.values()) {
            statis = s.getValue(topic);
            if (statis != null) {
                NavigableMap<Long, Long> delay = statis.getDelay(type);
                mapMergeableImpl.merge(delay);
            }
        }

        NavigableMap<Long, Long> result = mapMergeableImpl.adjustToMergeIfNecessary();
        return result.isEmpty() ? null : result;
    }

    @Override
    public Map<String, NavigableMap<Long, QpxData>> getQpxForServers(StatisType type) {

        checkSupported(type);
        HashMap<String, NavigableMap<Long, QpxData>> result = new HashMap<String, NavigableMap<Long, QpxData>>();
        for (Entry<String, S> entry : servers.entrySet()) {

            String serverIp = entry.getKey();
            S pssd = entry.getValue();
            result.put(serverIp, pssd.getQpx(type));
        }

        return result;
    }

    protected Map<String, NavigableMap<Long, QpxData>> getAllQpx(StatisType type, String topic, boolean includeTotal) {

        ConsumerTopicStatisData result = new ConsumerTopicStatisData();
        ConsumerTopicStatisData ctss;
        for (S s : servers.values()) {
            ctss = (ConsumerTopicStatisData) s.getValue(topic);
            if (ctss != null) {
                result.merge(ctss);
            }
        }
        return result.isEmpty() ? null : result.allQpx(type, includeTotal);
    }

    protected Map<String, NavigableMap<Long, Long>> getAllDelay(StatisType type, String topic, boolean includeTotal) {

        ConsumerTopicStatisData result = new ConsumerTopicStatisData();
        ConsumerTopicStatisData ctss;
        for (S s : servers.values()) {
            ctss = (ConsumerTopicStatisData) s.getValue(topic);
            if (ctss != null) {
                result.merge(ctss);
            }
        }
        return result.isEmpty() ? null : result.allDelay(type, includeTotal);
    }

    public String toString(String key) {

        return JsonBinder.getNonEmptyBinder().toPrettyJson(servers.get(key));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("clone not supported");
    }

    @Override
    public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {
        if (!keys.hasNextKey()) {
            throw new UnfoundKeyException(keys.toString());
        }

        String key = keys.getNextKey();
        S server;
        if (MonitorData.TOTAL_KEY.equals(key)) {
            MapMergeableImpl<Long, Long> mapMergeableImpl = new MapMergeableImpl<Long, Long>();
            for (S s : servers.values()) {
                NavigableMap<Long, Long> value = s.getDelayValue(keys, type);
                Collection<Long> values = value.values();
                Set<Long> set = new HashSet<Long>(values);
                if(set.size() == 1 && set.contains(0L)){
                    keys.reset();
                    continue;
                }
                mapMergeableImpl.merge(value);
                keys.reset();
            }
            return mapMergeableImpl.adjustToMergeIfNecessary();
        } else {
            server = servers.get(key);
        }

        if (server == null) {
            throw new UnfoundKeyException(key);
        }

        if (!keys.hasNextKey()) {
            return server.getDelay(type);
        }
        return server.getDelayValue(keys, type);
    }

    @Override
    public NavigableMap<Long, Statisable.QpxData> getQpsValue(CasKeys keys, StatisType type) {
        if (!keys.hasNextKey()) {
            throw new UnfoundKeyException(keys.toString());
        }

        String key = keys.getNextKey();
        S server;
        if (MonitorData.TOTAL_KEY.equals(key)) {
            MapMergeableImpl<Long, Statisable.QpxData> mapMergeableImpl = new MapMergeableImpl<Long, Statisable.QpxData>();
            for (S s : servers.values()) {
                NavigableMap<Long, Statisable.QpxData> value = s.getQpsValue(keys, type);
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
            return server.getQpx(type);
        }
        return server.getQpsValue(keys, type);
    }

    protected abstract S createValue();

}
