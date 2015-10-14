package com.dianping.swallow.common.server.monitor.data;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.Statisable.QpxData;


/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午9:42:26
 */
public interface ConsumerStatisRetriever extends StatisRetriever{


	Map<String, NavigableMap<Long, QpxData>> getQpxForAllConsumerId(String topic, StatisType type);
	
	Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(String topic, StatisType type);

	Set<String>  getConsumerIds(String topic);

	Map<String, NavigableMap<Long, QpxData>> getQpxForAllConsumerId(String topic, StatisType type, boolean includeTotal);
	
	Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(String topic, StatisType type, boolean includeTotal);

	Set<String>  getConsumerIds(String topic, boolean includeTotal);
	
	Map<String, Set<String>>  	  getAllTopics();


}
