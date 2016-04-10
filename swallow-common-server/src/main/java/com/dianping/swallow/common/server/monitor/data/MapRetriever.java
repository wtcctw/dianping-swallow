package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;

import java.util.NavigableMap;
import java.util.Set;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年7月8日 下午6:09:14
 */
public interface MapRetriever {

    Set<String> getKeys(CasKeys keys, StatisType type);

    Set<String> getKeys(CasKeys keys);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisData(CasKeys keys, RetrieveType retrieveType, StatisType type, Long startKey, Long stopKey);

}
