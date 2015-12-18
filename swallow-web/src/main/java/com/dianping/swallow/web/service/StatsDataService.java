package com.dianping.swallow.web.service;

import com.dianping.swallow.web.model.stats.StatsData;

/**
 * @author qi.yin
 *         2015/12/18  上午11:48.
 */
public interface StatsDataService {

    boolean removeLessThanTimeKey(long timeKey);

    StatsData findOldestData();
}
