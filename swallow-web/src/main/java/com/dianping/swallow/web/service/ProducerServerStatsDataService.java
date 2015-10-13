package com.dianping.swallow.web.service;

import java.util.Map;
import java.util.NavigableMap;

import com.dianping.swallow.web.model.stats.ProducerServerStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:39:51
 */
public interface ProducerServerStatsDataService {

	boolean insert(ProducerServerStatsData serverStatsData);

	Map<String, NavigableMap<Long, Long>> findSectionQpsData(long startKey, long endKey);
}
