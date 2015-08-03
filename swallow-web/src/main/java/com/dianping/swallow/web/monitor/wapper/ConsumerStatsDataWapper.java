package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午3:19:36
 */
public interface ConsumerStatsDataWapper {
	/**
	 * get all server statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ConsumerServerStatsData> getServerStatsDatas(long timeKey);

	/**
	 * get all consumerId statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	Map<String, List<ConsumerIdStatsData>> getConsumerIdStatsDatas(long timeKey);

	/**
	 * get one topic related consumerId statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ConsumerIdStatsData> getConsumerIdStatsDatas(String topicName, long timeKey);
	
	/**
	 * get consumerId related ip
	 * @param timeKey
	 * @return
	 */
	Set<String> getConsumerIdIps(String topicName, String consumerId);

	/**
	 * get topic related ip
	 * @param timeKey
	 * @return
	 */
	Set<String> getTopicIps(String topicName);
}
