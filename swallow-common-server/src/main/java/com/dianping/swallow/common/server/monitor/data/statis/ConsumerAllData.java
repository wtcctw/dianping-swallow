package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:45:46
 */
public class ConsumerAllData extends AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData> 
			implements ConsumerStatisRetriever{

	@Override
	protected Class<? extends ConsumerServerStatisData> getStatisClass() {
		
		return ConsumerServerStatisData.class;
	}


	
	protected Map<String, NavigableMap<Long, Long>> getAllQps(StatisType type, String topic) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		
		if(ctss == null){
			return null;
		}
		
		return ctss.allQpx(type);
	}

	protected Map<String, NavigableMap<Long, Long>> getAllDelay(StatisType type, String topic) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		
		if(ctss == null){
			return null;
		}
		
		return ctss.allDelay(type);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getSendQpxForAllConsumerId(
			String topic) {

		return getAllQps(StatisType.SEND, topic);
	}


	@Override
	public Map<String, NavigableMap<Long, Long>> getSendDelayForAllConsumerId(
			String topic) {
		
		return getAllDelay(StatisType.SEND, topic);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getAckQpxForAllConsumerId(
			String topic) {
		
		return getAllQps(StatisType.ACK, topic);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getAckDelayForAllConsumerId(
			String topic) {
		
		return getAllDelay(StatisType.ACK, topic);
	}

	@Override
	public Set<String> getConsumerIds(String topic) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		if(ctss == null){
			return null;
		}
		
		return ctss.keySet();
	}

}
