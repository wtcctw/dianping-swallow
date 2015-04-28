package com.dianping.swallow.web.monitor;

import java.util.List;

import com.dianping.swallow.common.server.monitor.visitor.QPX;

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
	StatsData getSaveDelay(String topic, int intervalTimeSeconds, long start, long end);

	StatsData getSaveDelay(String topic);

	StatsData getQpx(String topic, QPX qpx, int intervalTimeSeconds, long start, long end);
	
	StatsData getQpx(String topic, QPX qpx);
	
}
