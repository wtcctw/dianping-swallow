package com.dianping.swallow.web.monitor;

import java.util.Map;

import com.dianping.swallow.common.server.monitor.data.QPX;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface ProducerDataRetriever extends MonitorDataRetriever{
	
	StatsData getSaveDelay(String topic, long start, long end);

	StatsData getSaveDelay(String topic) throws Exception;

	StatsData getQpx(String topic, QPX qpx, long start, long end);
	
	StatsData getQpx(String topic, QPX qpx);

	Map<String, StatsData> getServerQpx(QPX qpx, long start, long end);

	Map<String, StatsData> getServerQpx(QPX qpx);

}
