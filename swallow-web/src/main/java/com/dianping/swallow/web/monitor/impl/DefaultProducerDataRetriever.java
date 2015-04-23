package com.dianping.swallow.web.monitor.impl;


import java.util.List;
import java.util.NavigableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.MonitorData;
import com.dianping.swallow.common.server.monitor.data.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitorFactory;
import com.dianping.swallow.common.server.monitor.visitor.ProducerMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.QPX;
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
	public StatsData getSaveDelay(String topic, int intervalTimeSeconds, long start, long end) {

		NavigableMap<Long, MonitorData> data = getData(topic, start, end);
		
		ProducerMonitorVisitor producerMonitorVisitor = MonitorVisitorFactory.buildProducerTopicVisitor(topic);

		visit(producerMonitorVisitor, data);
		
		List<Long> saveDelay = producerMonitorVisitor.buildSaveDelay(intervalTimeSeconds);
		
		return new StatsData(new ProducerStatsDataDesc(topic), saveDelay, start, intervalTimeSeconds) ;
	}




	@Override
	public StatsData getQpx(String topic, QPX qpx, int interval, long start,
			long end) {
		return null;
	}

	
	public static class ProducerServerData extends SwallowServerData{

		@Override
		protected Class<? extends MonitorData> getMonitorDataClass() {
			
			return ProducerMonitorData.class;
		}
		
	}

	@Override
	protected SwallowServerData createSwallowServerData() {
		
		return new ProducerServerData();
	}

	@Override
	protected Class<? extends SwallowServerData> getServerDataClass() {
		
		return ProducerServerData.class;
	}
}
