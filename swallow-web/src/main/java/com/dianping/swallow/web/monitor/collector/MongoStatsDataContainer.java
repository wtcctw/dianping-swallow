package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.model.stats.MongoStatsData;

import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 15/12/23  下午4:26.
 */
public interface MongoStatsDataContainer {

    void add(Long time, MongoStatsData mongoStatsData);

    void store();

    NavigableMap<Long, Long> retrieve(QPX qpx);

    boolean isEmpty();

    boolean isUpToMaxSize();
}
