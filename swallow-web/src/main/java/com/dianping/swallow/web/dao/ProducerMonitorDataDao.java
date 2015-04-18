package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月17日 下午4:10:44
 */
public interface ProducerMonitorDataDao extends Dao{
	
	
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
