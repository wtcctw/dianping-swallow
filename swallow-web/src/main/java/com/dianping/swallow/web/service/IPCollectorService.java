package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

/**
 * 
 * @author qi.yin
 *
 */
public interface IPCollectorService {

	/**
	 * add all ip
	 * 
	 * @param monitorData
	 */
	public void addStatisIps(MonitorData monitorData);

	/**
	 * get statis consumerServer ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getStatisConsumerServerIps();

	/**
	 * get statis producerServer ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getStatisProducerServerIps();

	/**
	 * get all statis ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getStatisIps();

	/**
	 * get producer server ips
	 * 
	 * @return
	 */
	public List<String> getProducerServerIps();

	/**
	 * get consumer server slave ips
	 * 
	 * @return
	 */
	public List<String> getConsumerServerSlaveIps();

	/**
	 * get consumer server master ips
	 * 
	 * @return
	 */
	public List<String> getConsumerServerMasterIps();
	
	/**
	 * clear statis producerServerIps
	 */
	public void clearStatisProducerServerIps();

	/**
	 * clear statis consumerServerIps
	 */
	public void clearStatisConsumerServerIps();

	/**
	 * topic consumerId related ip
	 * 
	 * @return
	 */
	public Set<String> getTopicConsumerIdIps(String topicName, String consumerId);

	/**
	 * producer topic related ip
	 * 
	 * @return
	 */
	public Set<String> getProducerTopicIps(String topicName);
	
	/**
	 * producer topic related ip
	 * 
	 * @return
	 */
	public Set<String> getConsumerTopicIps(String topicName);

	/**
	 * topicConsumerIdIps' key
	 * 
	 * @param topic
	 * @param consumerId
	 * @return
	 */
	public String getTopicConsumerIdKey(String topic, String consumerId);

}
