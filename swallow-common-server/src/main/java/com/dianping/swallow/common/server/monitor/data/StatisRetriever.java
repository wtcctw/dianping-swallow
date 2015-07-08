package com.dianping.swallow.common.server.monitor.data;


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

	NavigableMap<Long, Long> getQpxForTopic(String topic, StatisType type);
	
	NavigableMap<Long, Long> getDelayForTopic(String topic, StatisType type);

	Map<String, NavigableMap<Long, Long>> getQpxForServers(StatisType type);
}
