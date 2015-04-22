package com.dianping.swallow.web.monitor;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface ConsumerDataRetriever extends MonitorDataRetriever{
	
	StatsData getSendDelay(String topic, String consumerId, int interval, long start, long end);
	
	StatsData ackSendDelay(String topic, String consumerId, int interval, long start, long end);
	
}
