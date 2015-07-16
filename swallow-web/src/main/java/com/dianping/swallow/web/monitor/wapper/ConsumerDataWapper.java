package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;

/**
 *
 * @author qiyin
 *
 */
public interface ConsumerDataWapper {

	/**
	 * get all server statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	ConsumerServerStatsData getServerStatsData(long timeKey);

	/**
	 * get all topic statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ConsumerTopicStatsData> getTopicStatsData(long timeKey);

	/**
	 * get all consumerId statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	Map<String, List<ConsumerIdStatsData>> getConsumerIdStatsData(long timeKey);

	/**
	 * get one topic related consumerId statis data at timekey point
	 * @param timeKey
	 * @return
	 */
	List<ConsumerIdStatsData> getConsumerIdStatsData(String topicName, long timeKey);
	
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
