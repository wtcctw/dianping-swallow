package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.NavigableMap;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.visitor.ConsumerMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.MonitorTopicVisitor;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitorFactory;
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

	
	public static final String CAT_TYPE = "ConsumerDataRetriever";
	
	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic, int intervalTimeSeconds, long start, long end) {
		
		return getStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.TYPE_DELAY);
	}
	
	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, int intervalTimeSeconds, long start, long end) {
		
		return getStatsData(topic, intervalTimeSeconds, start, end, StatsDataType.TYPE_QPX);
	}

	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx, int intervalTimeSeconds, long start, long end) {
		
		Map<String, ConsumerDataPair> result = new HashMap<String, ConsumerDataPair>();
		Set<String> ips = getServerIps(start, end);
		
		for(String serverIp : ips){
			List<ConsumerDataPair> statsData = getStatsData(MonitorData.TOTAL_KEY, serverIp, intervalTimeSeconds, start, end, StatsDataType.TYPE_QPX, qpx);
			if(statsData.size() != 1){
				throw new IllegalStateException("total stats data type should be 1, but "  + statsData.size());
			}
			result.put(serverIp, statsData.get(0));
		}
		
		return result;
	}

	
	protected MonitorData createMonitorData() {
		return new ConsumerMonitorData();
	}
	
	private List<ConsumerDataPair> getStatsData(String topic, int intervalTimeSeconds, long start, long end, int type) {
		
		return getStatsData(topic, intervalTimeSeconds, start, end, type, QPX.SECOND);
	}

	private List<ConsumerDataPair> getStatsData(String topic, int intervalTimeSeconds, long start, long end, int type, QPX qpx) {
		
		return getStatsData(topic, null, intervalTimeSeconds, start, end, type, qpx);
	}

	private List<ConsumerDataPair> getStatsData(String topic, String serverIp, int intervalTimeSeconds, long start, long end, int type, QPX qpx) {
		
		if(logger.isDebugEnabled()){
			logger.debug("[getStatsData][begin]" + topic + "," + serverIp);
		}
		
		List<ConsumerDataPair> result = new LinkedList<ConsumerDataPair>();
		
		NavigableMap<Long, MonitorData> data = getData(topic, start, end, serverIp);
		
		long realStartTime = getRealStartTime(data, start, end);
		
		Set<String> consumerIds = getAllConsumerIds(topic, data);
		if(consumerIds.size() > 1){
			consumerIds.remove(MonitorData.TOTAL_KEY);
		}
		
		for(String consumerId : consumerIds){
			result.add(getConsumerIdDataPair(topic, consumerId, data, intervalTimeSeconds, realStartTime, end, type, qpx));
		}

		if(logger.isDebugEnabled()){
			logger.debug("[getStatsData][end]" + topic + "," + serverIp);
		}
		return result;
	}

	private String getConsumerIdSubTitle(String consumerId) {
		if(consumerId.equals(MonitorData.TOTAL_KEY)){
			return "全局平均";
		}
		return "consumerID:" + consumerId;
	}

	private ConsumerDataPair getConsumerIdDataPair(String topic, String consumerId,
			NavigableMap<Long, MonitorData> data, int intervalTimeSeconds,
			long start, long end, int type, QPX qpx) {
		
		if(logger.isDebugEnabled()){
			logger.debug("[getConsumerIdDataPair][begin]" + topic + "," + consumerId);
		}
		
		ConsumerMonitorVisitor consumerMonitorVisitor = MonitorVisitorFactory.buildConsumerConsumerIdVisitor(topic, consumerId);
		visit(consumerMonitorVisitor, data);
		
		List<Long> sendStats = null, ackStats = null;
		StatsData sendStatsData = null, ackStatsData = null;
		String subTitle = getConsumerIdSubTitle(consumerId);
		
		if(type == StatsDataType.TYPE_DELAY){
			sendStats = consumerMonitorVisitor.buildSendDelay(intervalTimeSeconds);
			ackStats = consumerMonitorVisitor.buildAckDelay(intervalTimeSeconds);
			sendStatsData = createStatsData(new ConsumerStatsDataDesc(topic, subTitle, StatsDataType.SEND_DELAY),  sendStats, start, end, data, intervalTimeSeconds, qpx);
			ackStatsData  = createStatsData(new ConsumerStatsDataDesc(topic, subTitle, StatsDataType.ACK_DELAY),  ackStats, start, end, data, intervalTimeSeconds, qpx);
		}else if(type == StatsDataType.TYPE_QPX){
			sendStats = consumerMonitorVisitor.buildSendQpx(intervalTimeSeconds, qpx);
			ackStats = consumerMonitorVisitor.buildAckQpx(intervalTimeSeconds, qpx);
			sendStatsData = createStatsData(new ConsumerStatsDataDesc(topic, subTitle, StatsDataType.SEND_QPX),  sendStats, start, end, data, intervalTimeSeconds, qpx);
			ackStatsData  = createStatsData(new ConsumerStatsDataDesc(topic, subTitle, StatsDataType.ACK_QPX),  ackStats, start, end, data, intervalTimeSeconds, qpx);
		}else{
			throw new IllegalArgumentException("unknown type:" + type);
		}

		if(logger.isDebugEnabled()){
			logger.debug("[getConsumerIdDataPair][end]" + topic + "," + consumerId);
		}

		return new ConsumerDataPair(consumerId, sendStatsData, ackStatsData);
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
	public List<ConsumerDataPair> getDelayForAllConsumerId(final String topic) throws Exception {
		
		SwallowCallableWrapper<List<ConsumerDataPair>> wrapper = new CatCallableWrapper<List<ConsumerDataPair>>(CAT_TYPE, "getDelayForAllConsumerId");
		
		return wrapper.doCallable(new Callable<List<ConsumerDataPair>>() {
			
			@Override
			public List<ConsumerDataPair> call() throws Exception {
				
				return getDelayForAllConsumerId(topic, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
			}
		});
	}


	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx) {
		
		return getQpxForAllConsumerId(topic, qpx, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}


	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx) {
		
		return getServerQpx(qpx, getDefaultInterval(), getDefaultStart(), getDefaultEnd());
	}



}
