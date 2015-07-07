package com.dianping.swallow.web.monitor;

import java.util.Map;


/**
 * 计算堆积消息量
 * @author mengwenchao
 *
 * 2015年5月28日 下午2:57:52
 */
public interface AccumulationRetriever extends Retriever{

	Map<String, StatsData> getAccumulationForAllConsumerId(String topic, long start, long end);
	
	Map<String, StatsData> getAccumulationForAllConsumerId(String topic);
	
	void registerListener(AccumulationListener listener);

}
