package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午3:19:36
 */
public interface ConsumerStatsDataWapper {
	/**
	 * get all server statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ConsumerServerStatsData> getServerStatsDatas(long timeKey);

	/**
	 * get total topic statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	ConsumerTopicStatsData getTotalTopicStatsData(long timeKey);

	/**
	 * get all consumerId statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ConsumerIdStatsData> getConsumerIdStatsDatas(long timeKey);

	/**
	 * get one topic related consumerId statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ConsumerIdStatsData> getConsumerIdStatsDatas(String topicName, long timeKey);

	/**
	 * get ConsumerId
	 * 
	 * @return
	 */
	List<String> getConusmerIdInfos();
	
	/**
	 * 
	 * @param isTotal
	 * @return
	 */
	Set<String> getTopics(boolean isTotal);

	/**
	 * 
	 * @param isTotal
	 * @return
	 */
	Set<String> getServerIps(boolean isTotal);

	/**
	 * get consumerId related ip
	 * 
	 * @param timeKey
	 * @return
	 */
	Set<String> getConsumerIdIps(String topicName, String consumerId, boolean isTotal);

	/**
	 * get topic related ip
	 * 
	 * @param timeKey
	 * @return
	 */
	Set<String> getTopicIps(String topicName, boolean isTotal);

	/**
	 * 
	 * @param isTotal
	 * @return
	 */
	Set<String> getIps(boolean isTotal);
}
