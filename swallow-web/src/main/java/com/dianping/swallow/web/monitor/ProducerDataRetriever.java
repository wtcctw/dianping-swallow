package com.dianping.swallow.web.monitor;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;

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
	
	List<ProducerTopicStatsData> getTopicStatis(long timeKey, StatisType type);
	
	ProducerServerStatsData getServerStatis(long timeKey, StatisType type);
}
