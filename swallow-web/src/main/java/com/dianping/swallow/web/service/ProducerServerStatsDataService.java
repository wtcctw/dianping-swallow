package com.dianping.swallow.web.service;

import java.util.List;
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

	boolean update(ProducerServerStatsData serverStatsData);

	int deleteById(String id);

	ProducerServerStatsData findById(String id);

	ProducerServerStatsData findByTimeKey(String ip, long timeKey);

	List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey);

	Map<String, NavigableMap<Long, Long>> findSectionQpsData(long startKey, long endKey);
}
