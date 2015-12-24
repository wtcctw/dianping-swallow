package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.model.stats.MongoStatsData;

import java.util.List;

/**
 * Author   mingdongli
 * 15/12/23  下午6:01.
 */
public interface MongoStatsDataDao {

    boolean insert(MongoStatsData mongoStatsData);

    boolean removeLessThanTimeKey(long timeKey);

    List<MongoStatsData> findSectionData(long startKey, long endKey);

    MongoStatsData findOldestData();
}
