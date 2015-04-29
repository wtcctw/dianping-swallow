package com.dianping.swallow.web.monitor;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.common.server.monitor.visitor.QPX;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午10:38:07
 */
public interface ConsumerDataRetriever extends MonitorDataRetriever{
	
	List<StatsData> getSendDelayForAllConsumerId(String topic, int intervalTimeSeconds, long start, long end);
	
	List<StatsData> getSendDelayForAllConsumerId(String topic);
	
	List<StatsData> getAckDelayForAllConsumerId(String topic, int intervalTimeSeconds, long start, long end);

	List<StatsData> getAckDelayForAllConsumerId(String topic);

	
	List<StatsData> getSendQpxForAllConsumerId(String topic, QPX qpx, int intervalTimeSeconds, long start, long end);
	
	List<StatsData> getSendQpxForAllConsumerId(String topic, QPX qpx);

	List<StatsData> getAckQpxForAllConsumerId(String topic, QPX qpx, int intervalTimeSeconds, long start, long end);
	
	List<StatsData> getAckdQpxForAllConsumerId(String topic, QPX qpx);
	

	Map<String, StatsData> getServerSendQpx(QPX qpx, int intervalTimeSeconds, long start, long end);

	Map<String, StatsData> getServerSendQpx(QPX qpx);
	
	Map<String, StatsData> getServerAckQpx(QPX qpx, int intervalTimeSeconds, long start, long end);

	Map<String, StatsData> getServerAckQpx(QPX qpx);
}

