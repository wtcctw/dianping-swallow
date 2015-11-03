package com.dianping.swallow.web.monitor;

import java.util.Map;
import java.util.NavigableMap;

import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoManager;


/**
 * 计算堆积消息量
 * @author mengwenchao
 *
 * 2015年5月28日 下午2:57:52
 */
public interface AccumulationRetriever extends Retriever{
	
	public static int ORDER = MongoManager.ORDER + 1;

	Map<String, StatsData> getAccumulationForAllConsumerId(String topic, long start, long end);
	
	Map<String, StatsData> getAccumulationForAllConsumerId(String topic);
	
	void registerListener(AccumulationListener listener);
	
	NavigableMap<Long, Long> getConsumerIdAccumulation(String topic, String consumerId);
	
	OrderStatsData getAccuOrderForAllConsumerId(int size, long start, long end);

	OrderStatsData getAccuOrderForAllConsumerId(int size);
}
