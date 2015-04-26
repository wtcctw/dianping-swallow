package com.dianping.swallow.common.server.monitor.collector;


import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:51:05
 */
public interface ConsumerCollector extends Collector{
	
	void sendMessage(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message);
	
	void ackMessage(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message);
}
