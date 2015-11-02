package com.dianping.swallow.common.server.monitor.data.statis;


import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.monitor.Mergeable;
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

//	@JsonIgnore
//	protected   S 			 total = null;

    protected final Set<StatisType> supportedTypes = new HashSet<StatisType>();

    public AbstractAllData(StatisType... types) {

        for (StatisType type : types) {
            supportedTypes.add(type);
        }
        //total = MapUtil.getOrCreate(servers, MonitorData.TOTAL_KEY, getStatisClass());
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

        //total.add(time, (T) added.getServerData());
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

        NavigableMap<Long, QpxData> result = new ConcurrentSkipListMap<Long, QpxData>();
        Statisable<M> statis;
        for (S s : servers.values()) {
            statis = s.getValue(topic);
            if (statis != null) {
                NavigableMap<Long, QpxData> qps = statis.getQpx(type);
                MapUtil.mergeMap(result, qps);
            }
        }

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

//    @Override
//    public Statisable getValue(CasKeys keys) {
//
//        return getValue(keys, null);
//    }


    private void checkSupported(StatisType type) {
        if (!supportedTypes.contains(type)) {
            throw new IllegalArgumentException("unsupported type:" + type + ", class:" + getClass());
        }
    }


    @Override
    public NavigableMap<Long, Long> getDelayForTopic(String topic, StatisType type) {

        checkSupported(type);

        NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
        Statisable<?> statis;
        for (S s : servers.values()) {
            statis = s.getValue(topic);
            if (statis != null) {
                NavigableMap<Long, Long> qps = statis.getDelay(type);
                MapUtil.mergeMapOfTypeLong(result, qps);
            }
        }

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

        ConsumerTopicStatisData ctss;
        for (S s : servers.values()) {
            ctss = (ConsumerTopicStatisData) s.getValue(topic);
            if (ctss != null) {
                return ctss.allQpx(type, includeTotal);
            }

        }
        return null;
    }

    protected Map<String, NavigableMap<Long, Long>> getAllDelay(StatisType type, String topic, boolean includeTotal) {

        ConsumerTopicStatisData ctss;
        for (S s : servers.values()) {
            ctss = (ConsumerTopicStatisData) s.getValue(topic);
            if (ctss != null) {
                return ctss.allDelay(type, includeTotal);
            }

        }
        return null;
//		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
//
//		if(ctss == null){
//			return null;
//		}
//
//		return ctss.allDelay(type, includeTotal);
    }

    public String toString(String key) {

        return JsonBinder.getNonEmptyBinder().toPrettyJson(servers.get(key));
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractAllData clone = (AbstractAllData) super.clone();
        clone.servers = new ConcurrentHashMap<String, S>(this.servers);
        return clone;
    }

    @Override
    public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {
        if (!keys.hasNextKey()) {
            throw new UnfoundKeyException(keys.toString());
        }

        String key = keys.getNextKey();
        S server;
        if (MonitorData.TOTAL_KEY.equals(key)) {
            NavigableMap<Long, Long> result = new ConcurrentSkipListMap<Long, Long>();
            for (S s : servers.values()) {
                NavigableMap<Long, Long> value = s.getDelayValue(keys, type);
                MapUtil.mergeMapOfTypeLong(result, value);
                keys.reset();
            }
            return result;
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
            NavigableMap<Long, Statisable.QpxData> result = new ConcurrentSkipListMap<Long, Statisable.QpxData>();
            for (S s : servers.values()) {
                NavigableMap<Long, Statisable.QpxData> value = s.getQpsValue(keys, type);
                MapUtil.mergeMap(result, value);
                keys.reset();
            }
            return result;
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

}
