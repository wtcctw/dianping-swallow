package com.dianping.swallow.web.monitor.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.dao.ProducerMonitorDao;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:09
 */
@Component
public class DefaultProducerDataRetriever extends AbstractMonitorDataRetriever implements ProducerDataRetriever{
	
	@Autowired
	private ProducerMonitorDao producerMonitorDao;

	@Override
	public StatsData getSaveDelay(String topic, int interval, long start, long end) {

		if(dataExistInMemory(topic, start, end)){
			
			return retriveMemoryDataStats(topic, interval, start, end);
		}
		return retrieveDbDataStats(topic, interval, start, end);
	}

	private StatsData retrieveDbDataStats(String topic, int interval, long start, long end) {
		
//		Queue<MonitorData> data = getMemoryData(topic, start, end)  
		return null;
	}


	private StatsData retriveMemoryDataStats(String topic, int interval,
			long start, long end) {
		
		return null;
	}

}
