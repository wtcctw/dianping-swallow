package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.internal.monitor.impl.AbstractMapMergeable;
import com.dianping.swallow.common.internal.monitor.impl.MapMergeableImpl;
import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisFunctionType;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.*;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午4:45:46
 */
public class ConsumerAllData extends AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData>
        implements ConsumerStatisRetriever {

    public ConsumerAllData() {
        super(StatisType.SEND, StatisType.ACK);
    }

    @Override
    protected Class<? extends ConsumerServerStatisData> getStatisClass() {

        return ConsumerServerStatisData.class;
    }


    @Override
    public Set<String> getConsumerIds(String topic) {
        return getConsumerIds(topic, true);
    }

    @Override
    public Map<String, NavigableMap<Long, StatisData>> getQpxForAllConsumerId(
            String topic, StatisType type) {

        return getQpxForAllConsumerId(topic, type, true);
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(
            String topic, StatisType type) {

        return getDelayForAllConsumerId(topic, type, true);
    }

    @Override
    public Map<String, NavigableMap<Long, StatisData>> getQpxForAllConsumerId(
            String topic, StatisType type, boolean includeTotal) {

        return getAllQpx(type, topic, includeTotal);
    }

    @Override
    public Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(
            String topic, StatisType type, boolean includeTotal) {

        return getAllDelay(type, topic, includeTotal);
    }

    @Override
    public Set<String> getConsumerIds(String topic, boolean includeTotal) {
        Set<String> consumerIds = new HashSet<String>();
        for (ConsumerServerStatisData csd : servers.values()) {
            if (csd != null) {
                Set<String> topics = csd.keySet(false);
                if (topics != null && topics.contains(topic)) {
                    ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) csd.getValue(topic);
                    if (ctss != null) {
                        consumerIds.addAll(ctss.keySet(includeTotal));
                    }
                }
            }
        }
        return consumerIds;
    }

    @Override
    public Map<String, Set<String>> getAllTopics() {

        Map<String, Set<String>> result = new HashMap<String, Set<String>>();

        Set<String> topics = getTopics(false);
        for (String topic : topics) {
            if (topic.equals(MonitorData.TOTAL_KEY)) {
                continue;
            }
            Set<String> consumerIds = getConsumerIds(topic, false);
            consumerIds.remove(MonitorData.TOTAL_KEY);
            result.put(topic, consumerIds);
        }
        return result;
    }

    protected Map<String, NavigableMap<Long, Long>> getAllDelay(StatisType type, String topic, boolean includeTotal) {

        Map<String, NavigableMap<Long, Long>> map = new ConcurrentSkipListMap<String, NavigableMap<Long, Long>>();
        Map<String, NavigableMap<Long, StatisData>> tmpResult = getAllQpx(type, topic, includeTotal);

        for (Map.Entry<String, NavigableMap<Long, StatisData>> entry : tmpResult.entrySet()) {
            map.put(entry.getKey(), convertData(entry.getValue(), StatisFunctionType.DELAY));
        }
        return map;
    }

    protected Map<String, NavigableMap<Long, StatisData>> getAllQpx(StatisType type, String topic, boolean includeTotal) {

        Map<String, NavigableMap<Long, StatisData>> map = new ConcurrentSkipListMap<String, NavigableMap<Long, StatisData>>();
        Map<String, AbstractMapMergeable<Long, StatisData>> tmpResult = new ConcurrentSkipListMap<String, AbstractMapMergeable<Long, StatisData>>();

        for (String server : servers.keySet()) {
            Set<String> cids = getKeys(new CasKeys(server, topic), type);
            for (String cid : cids) {
                if (MonitorData.TOTAL_KEY.equals(cid) && !includeTotal) {
                    continue;
                }
                NavigableMap<Long, StatisData> qpsValue = getStatisData(new CasKeys(server, topic, cid), type);
                if (qpsValue == null) {
                    continue;
                }

                if (tmpResult.get(cid) == null) {
                    AbstractMapMergeable<Long, StatisData> mapMergeableImpl = new MapMergeableImpl<Long, StatisData>();
                    mapMergeableImpl.merge(qpsValue);
                    tmpResult.put(cid, mapMergeableImpl);
                } else {
                    AbstractMapMergeable<Long, StatisData> originalQpsValue = tmpResult.get(cid);
                    originalQpsValue.merge(qpsValue);
                }
            }
        }

        for (Map.Entry<String, AbstractMapMergeable<Long, StatisData>> entry : tmpResult.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getToMerge());
        }
        return map;
    }

    @Override
    public ConsumerServerStatisData createValue() {
        return new ConsumerServerStatisData();
    }

}
