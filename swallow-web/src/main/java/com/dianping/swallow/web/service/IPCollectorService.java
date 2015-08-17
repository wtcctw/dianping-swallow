package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:42:18
 */
public interface IPCollectorService {

	/**
	 * add all ip
	 * 
	 * @param monitorData
	 */
	void addStatisIps(MonitorData monitorData);

	/**
	 * get statis consumerServer ip
	 * 
	 * @param monitorData
	 */
	Map<String, Long> getStatisConsumerServerIps();

	/**
	 * get statis producerServer ip
	 * 
	 * @param monitorData
	 */
	Map<String, Long> getStatisProducerServerIps();

	/**
	 * get all statis ip
	 * 
	 * @param monitorData
	 */
	Set<String> getStatisIps();

	/**
	 * get producer server ips
	 * 
	 * @return
	 */
	List<String> getProducerServerIps();

	/**
	 * get consumer server slave ips
	 * 
	 * @return
	 */
	List<String> getConsumerServerSlaveIps();

	/**
	 * get consumer server master ips
	 * 
	 * @return
	 */
	List<String> getConsumerServerMasterIps();

	/**
	 * get producer server master ips map
	 * 
	 * @return
	 */
	Map<String, String> getProducerServerIpsMap();

	/**
	 * get consumer server master ips map
	 * 
	 * @return
	 */
	Map<String, String> getConsumerServerMasterIpsMap();

	/**
	 * get consumer server slave ips map
	 * 
	 * @return
	 */
	Map<String, String> getConsumerServerSlaveIpsMap();

	/**
	 * topic consumerId related ip
	 * 
	 * @return
	 */
	Set<String> getTopicConsumerIdIps(String topicName, String consumerId);

	/**
	 * producer topic related ip
	 * 
	 * @return
	 */
	Set<String> getProducerTopicIps(String topicName);

	/**
	 * producer topic related ip
	 * 
	 * @return
	 */
	Set<String> getConsumerTopicIps(String topicName);

	/**
	 * topicConsumerIdIps' key
	 * 
	 * @param topic
	 * @param consumerId
	 * @return
	 */
	String getTopicConsumerIdKey(String topic, String consumerId);

}
