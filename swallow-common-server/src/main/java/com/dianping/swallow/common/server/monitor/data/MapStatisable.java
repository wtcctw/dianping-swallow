package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;
import java.util.NavigableMap;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月19日 下午5:04:56
 */
public interface MapStatisable<V> extends Statisable<V>, MapRetriever {

    @JsonIgnore
    NavigableMap<Long, StatisData> getStatisData(StatisType statisType, Object key);

    @JsonIgnore
    NavigableMap<Long, StatisData> getStatisData(RetrieveType retrieveType, StatisType statisType, Object key, Long startKey, Long stopKey);

    Map<String, NavigableMap<Long, StatisData>> allStatisData(StatisType type, boolean includeTotal);
}
