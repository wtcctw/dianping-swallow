package com.dianping.swallow.common.server.monitor.data;


import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;


/**
 * @author mengwenchao
 *         <p/>
 *         2015年5月21日 上午11:03:53
 */
public interface StatisRetriever extends MapRetriever {

    Set<String> getTopics(boolean includeTotal);

    NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, RetrieveType retrieveType, StatisType statisType);

    NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, RetrieveType retrieveType, StatisType statisType, Long startKey, Long stopKey);

    Map<String, NavigableMap<Long, StatisData>> getQpxForServers(StatisType type);

    NavigableMap<Long, StatisData> getMinData(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getMaxData(CasKeys keys, StatisType type);

    NavigableMap<Long, StatisData> getMoreThanData(CasKeys keys, StatisType type,Long startKey);

    NavigableMap<Long, StatisData> getLessThanData(CasKeys keys, StatisType type,Long stopKey);
}
