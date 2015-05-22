package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.NavigableMap;


/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午9:42:26
 */
public interface ProducerStatisRetriever extends StatisRetriever{
	
	NavigableMap<Long, Long> getSaveQpxForTopic(String topic);
	
	NavigableMap<Long, Long> getSaveDelayForTopic(String topic);

	Map<String, NavigableMap<Long, Long>> getSaveQpxForServers();
	
	NavigableMap<Long, Long> getSaveQpxForServer(String serverIp);

	
}
