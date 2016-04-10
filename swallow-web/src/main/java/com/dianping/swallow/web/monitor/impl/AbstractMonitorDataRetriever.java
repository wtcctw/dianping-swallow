package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.*;
import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractTotalMapStatisable;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.UnfoundKeyException;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.web.container.IpResourceContainer;
import com.dianping.swallow.web.container.ResourceContainer;
import org.springframework.beans.factory.annotation.Autowired;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.MonitorDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午11:04:30
 */
public abstract class AbstractMonitorDataRetriever<M extends Mergeable, T extends TotalMap<M>, S extends AbstractTotalMapStatisable<M, T>, V extends MonitorData>
        extends AbstractRetriever implements MonitorDataRetriever {

    private List<MonitorDataListener> statisListeners = new ArrayList<MonitorDataListener>();

    protected AbstractAllData<M, T, S, V> statis;

    private int intervalCount;

    @Autowired
    protected ResourceContainer resourceContainer;

    @Autowired
    protected IpResourceContainer ipResourceContainer;

    protected boolean dataExistInMemory(CasKeys keys, StatisType type, long start, long end) {
        NavigableMap<Long, StatisData> firstData = statis.getMinData(keys, type);
        if (firstData == null || firstData.isEmpty()) {
            return false;
        }
        Long firstKey = firstData.firstKey();
        if (firstKey != null) {
            if (getKey(start) + getKey(OFFSET_TIMESPAN) >= firstKey.longValue()) {
                return true;
            }
        }
        return false;
    }

    @PostConstruct
    public void postAbstractMonitorDataStats() {

        keepInMemoryCount = keepInMemoryHour * 3600 / AbstractCollector.SEND_INTERVAL;
        intervalCount = getSampleIntervalCount();

        statis = createServerStatis();
    }

    public String getDebugInfo(String server) {

        return statis.toString(server);
    }

    @Override
    protected void doBuild() {

        if (getKey(lastBuildTime) >= getKey(current)) {
            logger.warn("[doBuild][lastBuildTime key >= current key]" + lastBuildTime + "," + current);
            return;
        }

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doBuild");
        catWrapper.doAction(new SwallowAction() {

            @Override
            public void doAction() throws SwallowException {

                statis.build(QPX.SECOND, getKey(lastBuildTime), getKey(current), intervalCount);
            }
        });
        // 通知监听者
        doChangeNotify();
    }

    @Override
    protected void doRemove(final long toKey) {

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doRemove");
        catWrapper.doAction(new SwallowAction() {

            @Override
            public void doAction() throws SwallowException {
                statis.removeBefore(toKey);
            }
        });
    }

    protected abstract AbstractAllData<M, T, S, V> createServerStatis();

    protected StatsData getDelayInMemory(String topic, StatisType type, long start, long end) {

        NavigableMap<Long, StatisData> statisData = statis.getStatisDataForTopic(topic, type);
        NavigableMap<Long, Long> rawData = convertData(statisData, StatisFunctionType.DELAY);
        if (rawData != null) {
            long startKey = getKey(start);
            long endKey = getKey(end);
            rawData = rawData.subMap(startKey, true, endKey, true);
            rawData = fillStatsData(rawData, startKey, endKey);
        }
        return createStatsData(createDelayDesc(topic, type), rawData, start, end);
    }

    protected StatsData getIpDelayInMemory(String topic, String ip, StatisType type, long start, long end) {
        NavigableMap<Long, StatisData> statisData = statis.getStatisData(new CasKeys(TOTAL_KEY, topic, ip), type);
        NavigableMap<Long, Long> rawData = convertData(statisData, StatisFunctionType.DELAY);
        if (rawData != null) {
            long startKey = getKey(start);
            long endKey = getKey(end);
            rawData = rawData.subMap(startKey, true, endKey, true);
            rawData = fillStatsData(rawData, startKey, endKey);
        }
        return createStatsData(createDelayDesc(topic, type), rawData, start, end);
    }

    protected StatsData getQpxInMemory(String topic, StatisType type, long start, long end) {

        NavigableMap<Long, StatisData> statisData = statis.getStatisDataForTopic(topic, type);
        NavigableMap<Long, Long> rawData = convertData(statisData, StatisFunctionType.QPX);
        if (rawData != null) {
            long startKey = getKey(start);
            long endKey = getKey(end);
            rawData = rawData.subMap(startKey, true, endKey, true);
            rawData = fillStatsData(rawData, startKey, endKey);
        }
        return createStatsData(createQpxDesc(topic, type), rawData, start, end);
    }

    protected StatsData getIpQpxInMemory(String topic, String ip, StatisType type, long start, long end) {
        NavigableMap<Long, StatisData> statisData = statis.getStatisData(new CasKeys(TOTAL_KEY, topic, ip), type);
        NavigableMap<Long, Long> rawData = convertData(statisData, StatisFunctionType.QPX);
        if (rawData != null) {
            long startKey = getKey(start);
            long endKey = getKey(end);
            rawData = rawData.subMap(startKey, true, endKey, true);
            rawData = fillStatsData(rawData, startKey, endKey);
        }
        return createStatsData(createQpxDesc(topic, type), rawData, start, end);
    }

    protected Map<String, StatsData> getServerQpxInMemory(QPX qpx, StatisType type, long start, long end) {

        Map<String, StatsData> result = new HashMap<String, StatsData>();

        long startKey = getKey(start);
        long endKey = getKey(end);

        Map<String, NavigableMap<Long, StatisData>> serversQpx = statis.getQpxForServers(type);

        for (Entry<String, NavigableMap<Long, StatisData>> entry : serversQpx.entrySet()) {

            String serverIp = entry.getKey();
            NavigableMap<Long, Long> serverQpx = convertData(entry.getValue(), StatisFunctionType.QPX);
            if (serverQpx != null) {
                serverQpx = serverQpx.subMap(startKey, true, endKey, true);
                serverQpx = fillStatsData(serverQpx, startKey, endKey);
            }
            result.put(serverIp, createStatsData(createServerQpxDesc(serverIp, type), serverQpx, start, end));
        }

        return result;
    }

    protected NavigableMap<Long, QpxData> convertStatisDataToQpxVlaue(NavigableMap<Long, StatisData> map) {
        if (map == null) {
            return null;
        }
        NavigableMap<Long, QpxData> resultMap = new ConcurrentSkipListMap<Long, QpxData>();
        for (Map.Entry<Long, StatisData> entry : map.entrySet()) {
            resultMap.put(entry.getKey(), new QpxData(entry.getValue()));
        }
        return resultMap;
    }

    protected abstract StatsDataDesc createServerQpxDesc(String serverIp, StatisType type);

    protected abstract StatsDataDesc createServerDelayDesc(String serverIp, StatisType type);

    protected abstract StatsDataDesc createDelayDesc(String topic, StatisType type);

    //protected abstract StatsDataDesc createDelayDesc(String topic, String ip, StatisType type);

    protected abstract StatsDataDesc createQpxDesc(String topic, StatisType type);

    /**
     * 以发送消息的时间间隔为间隔，进行时间对齐
     *
     * @param currentTime
     * @return
     */
    protected static Long getCeilingTime(long currentTime) {

        return currentTime / 1000 / AbstractCollector.SEND_INTERVAL;
    }

    protected Set<String> getTopicsInMemory(long start, long end) {

        return statis.getTopics(true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void add(final MonitorData monitorData) {

        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doAdd");
        catWrapper.doAction(new SwallowAction() {

            @Override
            public void doAction() throws SwallowException {

                statis.add(monitorData.getKey(), (V) monitorData);
            }
        });
    }

    @Override
    public void registerListener(MonitorDataListener statisListener) {
        statisListeners.add(statisListener);
    }

    protected void doChangeNotify() {
        SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + "-doChangeNotify");
        catWrapper.doAction(new SwallowAction() {

            @Override
            public void doAction() throws SwallowException {

                for (MonitorDataListener statisListener : statisListeners) {
                    statisListener.achieveMonitorData();
                }
            }
        });

    }

    @Override
    public Set<String> getKeys(CasKeys keys) {
        return getKeys(keys, null);
    }

    @Override
    public Set<String> getKeys(CasKeys keys, StatisType type) {
        try {
            return statis.getKeys(keys, type);
        } catch (UnfoundKeyException e) {
            return null;
        }
    }

    @Override
    public NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type) {
        try {
            NavigableMap<Long, StatisData> statisData = statis.getStatisData(keys, type);
            return convertData(statisData, StatisFunctionType.DELAY);
        } catch (UnfoundKeyException e) {
            return null;
        }
    }

    @Override
    public NavigableMap<Long, Statisable.QpxData> getQpsValue(CasKeys keys, StatisType type) {

        try {
            NavigableMap<Long, StatisData> qpxMap = statis.getStatisData(keys, type);
            return convertStatisDataToQpxVlaue(qpxMap);
        } catch (UnfoundKeyException e) {
            return null;
        }
    }

    @Override
    public NavigableMap<Long, StatisData> getMinData(CasKeys keys, StatisType type) {
        return statis.getMinData(keys, type);
    }

    @Override
    public NavigableMap<Long, StatisData> getMaxData(CasKeys keys, StatisType type) {
        return statis.getMaxData(keys, type);

    }

    @Override
    public NavigableMap<Long, StatisData> getMoreThanData(CasKeys keys, StatisType type, Long startKey) {
        return statis.getMoreThanData(keys, type, startKey);
    }

    @Override
    public NavigableMap<Long, StatisData> getLessThanData(CasKeys keys, StatisType type, Long stopKey) {
        return statis.getLessThanData(keys, type, stopKey);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType) {
        return statis.getStatisData(keys, statisType);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType) {
        return statis.getStatisData(keys, retrieveType, statisType);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType, Long startKey, Long stopKey) {
        return statis.getStatisData(keys, RetrieveType.GENERAL_SECTION, statisType, startKey, stopKey);
    }

    @Override
    public NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey) {
        return statis.getStatisData(keys, retrieveType, statisType, startKey, stopKey);
    }

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
