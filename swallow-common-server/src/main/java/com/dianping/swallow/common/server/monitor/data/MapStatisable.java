package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.NavigableMap;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:04:56
 */
public interface MapStatisable<V> extends Statisable<V>, MapRetriever{

	@JsonIgnore
	NavigableMap<Long, Long> getDelay(StatisType type, Object key);

	@JsonIgnore
	NavigableMap<Long, QpxData> getQpx(StatisType type, Object key);
	
	Map<String, NavigableMap<Long, Long>> allDelay(StatisType type, boolean includeTotal);
	
	Map<String, NavigableMap<Long, QpxData>> allQpx(StatisType type, boolean includeTotal);

}
