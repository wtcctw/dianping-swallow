package com.dianping.swallow.web.service;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.model.stats.MongoStatsData;

import java.util.Map;
import java.util.NavigableMap;

/**
 * Author   mingdongli
 * 15/12/23  下午6:11.
 */
public interface MongoStatsDataService {

    boolean insert(MongoStatsData mongoStatsData);

    Map<String, NavigableMap<Long, Long>> findSectionQpsData(QPX qpx, long startKey, long endKey);
}
