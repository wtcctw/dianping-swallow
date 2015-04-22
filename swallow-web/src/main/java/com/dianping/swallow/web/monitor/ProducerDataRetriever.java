package com.dianping.swallow.web.monitor;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface ProducerDataRetriever extends MonitorDataRetriever{
	
	/**
	 * 获取延时统计信息
	 * @param interval
	 * @param start
	 * @param end
	 * @return
	 */
	StatsData getSaveDelay(String topic, int interval, long start, long end);


}
