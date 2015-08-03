package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

public interface ProducerStatsDataWapper {
	/**
	 * get all server statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ProducerServerStatsData> getServerStatsDatas(long timeKey);

	/**
	 * get all topic statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey);
	
	/**
	 * get topic related ip
	 * @param timeKey
	 * @return
	 */
	Set<String> getTopicIps(String topicName);
}
