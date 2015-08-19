package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.web.dao.ConsumerMonitorDao;

/**
 * @author mengwenchao
 *
 * 2015年4月17日 下午4:09:14
 */
@Component
public class DefaultConsumerMonitorDao extends AbstractMonitorDao implements ConsumerMonitorDao{
	
	public static final String CONSUMER_MONITOR_COLLECTION_NAME = "CONSUMER_MONITOR_DATA";
	

	@Override
	public void saveConsumerMonotorData(ConsumerMonitorData consumerMonitorData) {
		
		mongoTemplate.save(consumerMonitorData, CONSUMER_MONITOR_COLLECTION_NAME);
	}

	@Override
	public void saveConsumerMonotorData(
			List<ConsumerMonitorData> consumerMonitorDatas) {
		
		for(ConsumerMonitorData consumerMonitorData : consumerMonitorDatas){
			saveConsumerMonotorData(consumerMonitorData);
		}
		
	}


}
