package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;


/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午9:42:26
 */
public interface ConsumerStatisRetriever extends StatisRetriever{


	Map<String, NavigableMap<Long, Long>> getSendQpxForAllConsumerId(String topic);
	
	Map<String, NavigableMap<Long, Long>> getSendDelayForAllConsumerId(String topic);

	Map<String, NavigableMap<Long, Long>> getAckQpxForAllConsumerId(String topic);
	
	Map<String, NavigableMap<Long, Long>> getAckDelayForAllConsumerId(String topic);

	Set<String>  getConsumerIds(String topic);
}
