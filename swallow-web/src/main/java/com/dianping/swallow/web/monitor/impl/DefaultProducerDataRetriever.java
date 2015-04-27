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
import com.dianping.swallow.web.monitor.StatsDataType;

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
		
		return buildStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.SAVE_DELAY);
	}
	
	@Override
	public StatsData getQpx(String topic, QPX qpx, int intervalTimeSeconds, long start,
			long end) {
		
		return buildStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.SAVE_QPX, qpx);
	}

	private StatsData buildStatsData(String topic, int intervalTimeSeconds,
			long start, long end, StatsDataType type) {
		return buildStatsData(topic, intervalTimeSeconds, start, end, type, QPX.SECOND);
	}

	private StatsData buildStatsData(String topic, int intervalTimeSeconds,
			long start, long end, StatsDataType type, QPX qpx) {
		
		NavigableMap<Long, MonitorData> data = getData(topic, start, end);
		
		ProducerMonitorVisitor producerMonitorVisitor = MonitorVisitorFactory.buildProducerTopicVisitor(topic);
		
		visit(producerMonitorVisitor, data);
		
		List<Long> result = null;
		
		switch(type){
			case SAVE_DELAY:
				result = producerMonitorVisitor.buildSaveDelay(intervalTimeSeconds);
				break;
			case SAVE_QPX:
				result = producerMonitorVisitor.buildSaveQpx(intervalTimeSeconds, qpx);
				break;
			default:
				throw new IllegalArgumentException("unknown type:" + type);
		}
		
		return new StatsData(new ProducerStatsDataDesc(topic, type), result, 
				getRealStartTime(data, start, end), 
				getRealIntervalSeconds(intervalTimeSeconds, qpx)) ;
		
	}


	@Override
	public StatsData getQpx(String topic, QPX qpx) {
		
		return getQpx(topic, qpx, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
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

	@Override
	public StatsData getSaveDelay(String topic) {
		
		return getSaveDelay(topic, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}


	@Override
	protected MonitorData createMonitorData() {
		return new ProducerMonitorData();
	}


}
