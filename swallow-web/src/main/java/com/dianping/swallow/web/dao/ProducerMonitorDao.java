package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.common.server.monitor.data.ProducerMonitorData;


/**
 * @author mengwenchao
 *
 * 2015年4月17日 下午4:10:44
 */
public interface ProducerMonitorDao extends MonitorDao{
	
	
	/**
	 * 保存生产者服务器监控信息
	 * @param producerMonitorData
	 */
	void saveProducerMonotorData(ProducerMonitorData producerMonitorData);

	
	/**
	 * 保存生产者服务器监控信息
	 * @param producerMonitorData
	 */
	void saveProducerMonotorData(List<ProducerMonitorData> producerMonitorDatas);


}
