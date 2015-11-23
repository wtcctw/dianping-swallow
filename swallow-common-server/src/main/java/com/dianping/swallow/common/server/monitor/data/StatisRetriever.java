package com.dianping.swallow.common.server.monitor.data;


import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.StatisData;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;


/**
 * @author mengwenchao
 *
 * 2015年5月21日 上午11:03:53
 */
public interface StatisRetriever extends MapRetriever{
	
	Set<String> getTopics(boolean includeTotal);

	NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type);

	NavigableMap<Long, Long> getDelayValue(CasKeys keys, StatisType type, Long startKey, Long stopKey);

	//invoke getStatisdata()
	NavigableMap<Long, StatisData> getQpsValue(CasKeys keys, StatisType type);

	NavigableMap<Long, StatisData> getQpsValue(CasKeys keys, StatisType type,Long startKey, Long stopKey);

	NavigableMap<Long, StatisData> getFirstValue(CasKeys keys, StatisType type);

	NavigableMap<Long, StatisData> getLastValue(CasKeys keys, StatisType type);

	//user-friendly,no need to change code
	NavigableMap<Long, Long> getDelay(StatisType type);

	NavigableMap<Long, StatisData> getQpx(StatisType type);

	NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, StatisType type);

	NavigableMap<Long, StatisData> getStatisDataForTopic(String topic, StatisType type, Long startKey, Long stopKey);

	Map<String, NavigableMap<Long, StatisData>> getQpxForServers(StatisType type);
}
