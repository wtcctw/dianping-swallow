package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;

/**
 *
 * @author qiyin
 *
 */
public interface ProducerDataWapper {

	/**
	 * get all server statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	ProducerServerStatsData getServerStatsData(long timeKey);

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
