package com.dianping.swallow.common.internal.monitor.collector;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:51:05
 */
public interface ProducerCollector extends Collector{
	
	void addMessage(String topic, String producerIp, long messageId, long sendTime, long saveTime);

}
