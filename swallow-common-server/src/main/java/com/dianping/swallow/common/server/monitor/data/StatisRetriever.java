package com.dianping.swallow.common.server.monitor.data;


import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;


/**
 * @author mengwenchao
 *
 * 2015年5月21日 上午11:03:53
 */
public interface StatisRetriever extends MapRetriever{
	
	Set<String> getTopics(boolean includeTotal);

	NavigableMap<Long, QpxData> getQpxForTopic(String topic, StatisType type);
	
	NavigableMap<Long, Long> getDelayForTopic(String topic, StatisType type);

	Map<String, NavigableMap<Long, QpxData>> getQpxForServers(StatisType type);
}
