package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月17日 下午4:10:44
 */
public interface ConsumerMonitorDao extends MonitorDao{
	
	
	/**
	 * 保存消费者服务器监控信息
	 * @param consumerMonitorData
	 */
	void saveConsumerMonotorData(ConsumerMonitorData consumerMonitorData);

	
	/**
	 * 保存消费者服务器监控信息
	 * @param consumerMonitorData
	 */
	void saveConsumerMonotorData(List<ConsumerMonitorData> consumerMonitorDatas);

}
