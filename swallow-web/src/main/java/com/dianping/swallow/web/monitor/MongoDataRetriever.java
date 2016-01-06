package com.dianping.swallow.web.monitor;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.web.monitor.collector.MongoStatsDataCollector;

import java.util.Map;

/**
 * Author   mingdongli
 * 16/1/6  下午4:54.
 */
public interface MongoDataRetriever {

    Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpx(QPX qpx, long start, long end);

    Map<MongoStatsDataCollector.MongoStatsDataKey, StatsData> getMongoQpx(QPX qpx);

    String getMongoDebugInfo(String server);

    StatsDataDesc createMongoQpxDesc(MongoStatsDataCollector.MongoStatsDataKey server, StatisType type);
}
