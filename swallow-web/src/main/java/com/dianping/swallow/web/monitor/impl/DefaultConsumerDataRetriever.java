package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:09
 */
@Component
public class DefaultConsumerDataRetriever extends AbstractMonitorDataRetriever<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData> 
			implements ConsumerDataRetriever{

	
	public static final String CAT_TYPE = "ConsumerDataRetriever";
	
	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic,  long start, long end) {
		
		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		
		Map<String, NavigableMap<Long, Long>> sendDelays = retriever.getDelayForAllConsumerId(topic, StatisType.SEND);
		Map<String, NavigableMap<Long, Long>> ackDelays = retriever.getDelayForAllConsumerId(topic, StatisType.ACK);

		removeTotal(sendDelays);
		removeTotal(ackDelays);
		
		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
		
		if(sendDelays != null){
			for(Entry<String, NavigableMap<Long, Long>> entry : sendDelays.entrySet()){
				
				String consumerId =  entry.getKey();
				NavigableMap<Long, Long> send = entry.getValue();
				NavigableMap<Long, Long> ack = ackDelays.get(consumerId);
				
				StatsData sendStatis = new StatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.SEND), getValue(send), getStartTime(send, start, end), getDefaultInterval());
				StatsData ackStatis = new StatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.ACK), getValue(ack), getStartTime(ack, start, end), getDefaultInterval());
				
				result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
			} 
		}
		
		return result;
	}
	
	private void removeTotal(Map<String, NavigableMap<Long, Long>> data) {
		
		if(data.size() > 1){
			data.remove(MonitorData.TOTAL_KEY);
		}
	}

	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx,  long start, long end) {
		
		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		
		Map<String, NavigableMap<Long, Long>> sendQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.SEND);
		Map<String, NavigableMap<Long, Long>> ackQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.ACK);
		
		removeTotal(sendQpxs);
		removeTotal(ackQpxs);
		
		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
		
		if(sendQpxs != null){
			for(Entry<String, NavigableMap<Long, Long>> entry : sendQpxs.entrySet()){
				
				String consumerId =  entry.getKey();
				NavigableMap<Long, Long> send = entry.getValue();
				NavigableMap<Long, Long> ack = ackQpxs.get(consumerId);
				
				StatsData sendStatis = new StatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.SEND), getValue(send), getStartTime(send, start, end), getDefaultInterval());
				StatsData ackStatis = new StatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.ACK), getValue(send), getStartTime(ack, start, end), getDefaultInterval());
				
				result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
			} 
		}		
		return result;
	}

	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx,  long start, long end) {
		
		Map<String, StatsData> sendQpxs = null;
		
		Map<String, StatsData> ackQpxs = null;
		
		if(dataExistInMemory(start, end)){
			sendQpxs = getServerQpxInMemory(qpx, StatisType.SEND, start, end);
			ackQpxs = getServerQpxInMemory(qpx, StatisType.ACK, start, end);
		}else{
			sendQpxs = getServerQpxInDb(qpx, StatisType.SEND, start, end);
			ackQpxs = getServerQpxInDb(qpx, StatisType.ACK, start, end);
		}
		
		Map<String, ConsumerDataPair> result = new HashMap<String, ConsumerDataRetriever.ConsumerDataPair>();
		for(Entry<String, StatsData> entry : sendQpxs.entrySet()){
			
			String serverIp = entry.getKey();
			StatsData sendQpx = entry.getValue();
			StatsData ackQpx = ackQpxs.get(serverIp);
			result.put(serverIp, new ConsumerDataPair(getConsumerIdSubTitle(MonitorData.TOTAL_KEY), sendQpx, ackQpx));
		}
		
		return result;
	}


	private String getConsumerIdSubTitle(String consumerId) {
		if(consumerId.equals(MonitorData.TOTAL_KEY)){
			return "全局平均";
		}
		return "consumerID:" + consumerId;
	}


	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx) {
		
		return getQpxForAllConsumerId(topic, qpx, getDefaultStart(), getDefaultEnd());
	}


	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx) {
		
		return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic)
			throws Exception {
		
		return getDelayForAllConsumerId(topic, getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData> createServerStatis() {
		
		return new ConsumerAllData();
	}

	@Override
	protected StatsDataDesc createDelayDesc(String topic, StatisType type) {
		
		return new ConsumerStatsDataDesc(topic, type.getDelayDetailType());
	}

	@Override
	protected StatsDataDesc createQpxDesc(String topic, StatisType type) {
		
		return new ConsumerStatsDataDesc(topic, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

		return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerDelayDesc(String serverIp,
			StatisType type) {

		return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
	}

	protected StatsDataDesc createConsumerIdDelayDesc(String topic, String consumerId,
			StatisType type) {

		return new ConsumerStatsDataDesc(topic, consumerId, type.getDelayDetailType());
	}

	protected StatsDataDesc createConsumerIdQpxDesc(String topic, String consumerId,
			StatisType type) {

		return new ConsumerStatsDataDesc(topic, consumerId, type.getQpxDetailType());
	}


}
