package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.monitor.MongoDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.monitor.collector.MongoStatsDataCollector;
import com.dianping.swallow.web.monitor.collector.MongoStatsDataContainer;
import com.dianping.swallow.web.service.MongoStatsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 16/1/6  下午4:52.
 */
@Component
public class DefaultMongoDataRetriever extends AbstractMonitorDataRetriever<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> implements MongoDataRetriever {

    @Autowired
    private MongoStatsDataCollector mongoStatsDataCollector;

    @Autowired
    private MongoStatsDataService mongoStatsDataService;

    @Override
    protected AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> createServerStatis() {
        return new ProducerAllData();
    }

    @Override
    protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createDelayDesc(String topic, StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    @Override
    protected StatsDataDesc createQpxDesc(String topic, StatisType type) {
        throw new UnsupportedOperationException("not support");
    }

    private boolean dataExistInMemory(CasKeys keys, long start, long end) {
        return dataExistInMemory(keys, StatisType.SAVE, start, end);
    }

    @Override
    public Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpx(QPX qpx) {
        return getMongoQpx(qpx, getDefaultStart(), getDefaultEnd());
    }

    @Override
    public Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpx(QPX qpx, long start, long end) {

        if (dataExistInMemory(new CasKeys(TOTAL_KEY, TOTAL_KEY), start, end)) {
            return getMongoQpxInMemory(qpx, StatisType.SAVE, start, end);
        }

        return getMongoQpxInDb(qpx, StatisType.SAVE, start, end);
    }

    protected Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpxInMemory(QPX qpx, StatisType type, long start, long end) {

        Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> result = new HashMap<MongoStatsDataCollector.MongoStatsDataKey, StatsData>();

        long startKey = getKey(start);
        long endKey = getKey(end);

        Map<MongoStatsDataCollector.MongoStatsDataKey, NavigableMap<Long, Long>> mongoQpxs = mongoStatsDataCollector.retrieveAllQpx(qpx);

        for (Map.Entry<MongoStatsDataCollector.MongoStatsDataKey, NavigableMap<Long, Long>> entry : mongoQpxs.entrySet()) {

            MongoStatsDataCollector.MongoStatsDataKey mongoIp = entry.getKey();
            NavigableMap<Long, Long> mongoQpx = entry.getValue();
            if (mongoQpx != null) {
                mongoQpx = mongoQpx.subMap(startKey, true, endKey, true);
                mongoQpx = fillStatsData(mongoQpx, startKey, endKey);
            }
            result.put(mongoIp, createStatsData(createMongoQpxDesc(mongoIp, type), mongoQpx, start, end));
        }

        return result;
    }

    protected Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpxInDb(QPX qpx, StatisType type, long start, long end) {
        Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> result = new HashMap<MongoStatsDataCollector.MongoStatsDataKey, StatsData>();

        long startKey = getKey(start);
        long endKey = getKey(end);
        Map<String, NavigableMap<Long, Long>> statsDataMaps = mongoStatsDataService.findSectionQpsData(qpx, startKey, endKey);

        for (Map.Entry<String, NavigableMap<Long, Long>> statsDataMap : statsDataMaps.entrySet()) {
            String serverIp = statsDataMap.getKey();

            NavigableMap<Long, Long> statsData = statsDataMap.getValue();
            statsData = fillStatsData(statsData, startKey, endKey);
            MongoStatsDataCollector.MongoStatsDataKey mongoStatsDataKey = mongoStatsDataCollector.generateMongoStatsDataKey(serverIp);
            result.put(mongoStatsDataKey, createStatsData(createMongoQpxDesc(mongoStatsDataKey, type), statsData, start, end));

        }
        return result;
    }

    @Override
    public String getMongoDebugInfo(String server) {
        Map<MongoStatsDataCollector.MongoStatsDataKey, MongoStatsDataContainer> mongoStatsDataMap = mongoStatsDataCollector.getMongoStatsDataMap();
        String mongoStatsDataString = "";
        if(mongoStatsDataMap != null){
            mongoStatsDataString = mongoStatsDataMap.toString();
        }
        Map<String, String> topicToMongo = mongoStatsDataCollector.getTopicToMongo();
        String topicToMongoString = "";
        if(topicToMongo != null){
            topicToMongoString = topicToMongo.toString();
        }

        return mongoStatsDataString + "\n----------------\n" + topicToMongoString;

    }

    @Override
    public StatsDataDesc createMongoQpxDesc(MongoStatsDataCollector.MongoStatsDataKey server, StatisType type) {
        return new MongoStatsDataDesc(server.getCatalog(), server.getIp(), type.getQpxDetailType());
    }
}
