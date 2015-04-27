package com.dianping.swallow.web.monitor.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.visitor.ConsumerMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.MonitorTopicVisitor;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitorFactory;
import com.dianping.swallow.common.server.monitor.visitor.QPX;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataType;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:09
 */
@Component
public class DefaultConsumerDataRetriever extends AbstractMonitorDataRetriever implements ConsumerDataRetriever{

	
	@Override
	public List<StatsData> getSendDelayForAllConsumerId(String topic, int intervalTimeSeconds, long start, long end) {
		
		return createStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.SEND_DELAY);
	}

	@Override
	public List<StatsData> getAckDelayForAllConsumerId(String topic,
			int intervalTimeSeconds, long start, long end) {
		
		return createStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.ACK_DELAY);
	}
	
	@Override
	public List<StatsData> getSendQpxForAllConsumerId(String topic, QPX qpx,
			int intervalTimeSeconds, long start, long end) {
		
		return createStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.SEND_QPX, qpx);
	}
	
	@Override
	public List<StatsData> getAckQpxForAllConsumerId(String topic, QPX qpx,
			int intervalTimeSeconds, long start, long end) {
		return createStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.ACK_QPX, qpx);
	}

	private List<StatsData> createStatsData(String topic, int intervalTimeSeconds, long start, long end, StatsDataType type) {
		
		return createStatsData(topic, intervalTimeSeconds, start, end, type, QPX.SECOND);
	}

	private List<StatsData> createStatsData(String topic, int intervalTimeSeconds, long start, long end, StatsDataType type, QPX qpx) {
		
		List<StatsData> result = new LinkedList<StatsData>();
		
		NavigableMap<Long, MonitorData> data = getData(topic, start, end);
		
		long realStartTime = getRealStartTime(data, start, end);
		
		Set<String> consumerIds = getAllConsumerIds(topic, data);
		if(consumerIds.size() > 1){
			consumerIds.remove(MonitorData.TOTAL_KEY);
		}
		
		for(String consumerId : consumerIds){
			result.add(getConsumerIdStats(topic, consumerId, data, intervalTimeSeconds, realStartTime, end, type, qpx));
		}
		return result;
	}

	private String getConsumerIdSubTitle(String consumerId) {
		if(consumerId.equals(MonitorData.TOTAL_KEY)){
			return "全局平均";
		}
		return "consumerID:" + consumerId;
	}

	private StatsData getConsumerIdStats(String topic, String consumerId,
			NavigableMap<Long, MonitorData> data, int intervalTimeSeconds,
			long start, long end, StatsDataType type, QPX qpx) {
		
		ConsumerMonitorVisitor consumerMonitorVisitor = MonitorVisitorFactory.buildConsumerConsumerIdVisitor(topic, consumerId);
		visit(consumerMonitorVisitor, data);
		List<Long> stats = null;
		
		switch(type){
			case SEND_DELAY:
				stats = consumerMonitorVisitor.buildSendDelay(intervalTimeSeconds);
				break;
			case ACK_DELAY:
				stats = consumerMonitorVisitor.buildAckDelay(intervalTimeSeconds);
				break;
			case SEND_QPX:
				stats = consumerMonitorVisitor.buildSendQpx(intervalTimeSeconds, qpx);
				break;
			case ACK_QPX:
				stats = consumerMonitorVisitor.buildAckQpx(intervalTimeSeconds, qpx);
				break;
			default:
				throw new IllegalArgumentException("unknown type:" + type);
		}
		String subTitle = getConsumerIdSubTitle(consumerId);
		return new StatsData(new ConsumerStatsDataDesc(topic, subTitle, type) , stats, start, getRealIntervalSeconds(intervalTimeSeconds));
	}


	private Set<String> getAllConsumerIds(final String topic, NavigableMap<Long, MonitorData> data) {
		
		final Set<String> consumerIds = new HashSet<String>();
		
		for(Entry<Long, MonitorData> entry : data.entrySet()){
			
			ConsumerMonitorData consumerMonitorData = (ConsumerMonitorData) entry.getValue();
			consumerMonitorData.accept(new MonitorTopicVisitor() {
				
				@SuppressWarnings("unchecked")
				@Override
				public void visitTopic(@SuppressWarnings("rawtypes") TotalMap visitorData) {
					if(visitorData != null){
						consumerIds.addAll(visitorData.keySet());
					}
				}
				
				@Override
				public String getVisitTopic() {
					return topic;
				}
			});
		}
		
		return consumerIds;
	}

	@Override
	protected SwallowServerData createSwallowServerData() {
		return new ConsumerSwallowServerData();
	}

	@Override
	protected Class<? extends SwallowServerData> getServerDataClass() {
		
		return ConsumerSwallowServerData.class;
	}
	
	
	public static class ConsumerSwallowServerData extends SwallowServerData{

		@Override
		protected Class<? extends MonitorData> getMonitorDataClass() {
			return ConsumerMonitorData.class;
		}
	}



	@Override
	public List<StatsData> getSendDelayForAllConsumerId(String topic) {
		
		return getSendDelayForAllConsumerId(topic, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<StatsData> getAckDelayForAllConsumerId(String topic) {
		
		return getAckDelayForAllConsumerId(topic, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected MonitorData createMonitorData() {
		return new ConsumerMonitorData();
	}

	@Override
	public List<StatsData> getSendQpxForAllConsumerId(String topic, QPX qpx) {
		
		return getSendQpxForAllConsumerId(topic, qpx, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}


	@Override
	public List<StatsData> getAckdQpxForAllConsumerId(String topic, QPX qpx) {
		
		return getAckQpxForAllConsumerId(topic, qpx, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}

}
