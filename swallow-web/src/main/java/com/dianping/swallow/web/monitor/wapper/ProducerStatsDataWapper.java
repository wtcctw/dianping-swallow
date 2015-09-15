package com.dianping.swallow.web.monitor.wapper;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

public interface ProducerStatsDataWapper {
	/**
	 * get all server statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ProducerServerStatsData> getServerStatsDatas(long timeKey);

	/**
	 * get all topic statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ProducerTopicStatsData> getTopicStatsDatas(long timeKey);

	/**
	 * get all ip statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ProducerIpStatsData> getIpStatsDatas(long timeKey);

	/**
	 * get all ip statis data at timekey point and topic
	 * 
	 * @param topicName
	 * @param timeKey
	 * @return
	 */
	List<ProducerIpStatsData> getIpStatsDatas(String topicName, long timeKey);

	/**
	 * get all ip group statis data at timekey point
	 * 
	 * @param timeKey
	 * @return
	 */
	List<ProducerIpGroupStatsData> getIpGroupStatsDatas(long timeKey);

	/**
	 * get all ip group statis data at timekey point and topic
	 * 
	 * @param topicName
	 * @param timeKey
	 * @return
	 */
	ProducerIpGroupStatsData getIpGroupStatsData(String topicName, long timeKey);

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
	Set<String> getTopics(boolean isTotal);

	/**
	 * 
	 * @param isTotal
	 * @return
	 */
	Set<String> getServerIps(boolean isTotal);

	/**
	 * 
	 * @param isTotal
	 * @return
	 */
	Set<String> getIps(boolean isTotal);
}
