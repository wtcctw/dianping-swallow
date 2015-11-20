package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.server.monitor.data.structure.StatisData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.NavigableMap;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:04:56
 */
public interface MapStatisable<V> extends Statisable<V>, MapRetriever{

	@JsonIgnore
	NavigableMap<Long, StatisData> getStatisData(StatisType type, Object key);

	@JsonIgnore
	NavigableMap<Long, StatisData> getStatisData(StatisType type, Object key, Long startKey, Long stopKey);

	Map<String, NavigableMap<Long, StatisData>> allStatisData(StatisType type, boolean includeTotal);
}
