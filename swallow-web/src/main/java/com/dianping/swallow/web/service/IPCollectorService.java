package com.dianping.swallow.web.service;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;

/**
 * 
 * @author qi.yin
 *
 */
public interface IPCollectorService extends SwallowService {

	/**
	 * add all ip
	 * 
	 * @param monitorData
	 */
	public void addIps(MonitorData monitorData);

	/**
	 * add producerServer ip
	 * 
	 * @param monitorData
	 */
	public void addProducerServerIps(ProducerMonitorData producerMonitorData);

	/**
	 * add consumerServer ip
	 * 
	 * @param monitorData
	 */
	public void addConsumerServerIps(ConsumerMonitorData consumerMonitorData);

	/**
	 * get consumerServer ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getConsumerServerIps();

	/**
	 * get producerServer ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getProducerServerIps();

	/**
	 * get all ip
	 * 
	 * @param monitorData
	 */
	public Set<String> getIps();

	/**
	 * get cmdb producers
	 * 
	 * @return
	 */
	public Map<String, String> getCmdbProducers();

	/**
	 * get cmdb consumer slaves
	 * 
	 * @return
	 */
	public Map<String, String> getCmdbConsumerSlaves();

	/**
	 * get cmdb consumer masters
	 * 
	 * @return
	 */
	public Map<String, String> getCmdbConsumerMasters();

}
