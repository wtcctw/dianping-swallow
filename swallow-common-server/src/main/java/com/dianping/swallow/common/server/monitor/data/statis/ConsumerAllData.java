package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:45:46
 */
public class ConsumerAllData extends AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData>
			implements ConsumerStatisRetriever{

	public ConsumerAllData(){
		super(StatisType.SEND, StatisType.ACK);
	}
	
	@Override
	protected Class<? extends ConsumerServerStatisData> getStatisClass() {
		
		return ConsumerServerStatisData.class;
	}


	@Override
	public Set<String> getConsumerIds(String topic) {
		return getConsumerIds(topic, true);
	}

	@Override
	public Map<String, NavigableMap<Long, QpxData>> getQpxForAllConsumerId(
			String topic, StatisType type) {
		
		return getQpxForAllConsumerId(topic, type, true);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(
			String topic, StatisType type) {
		
		return getDelayForAllConsumerId(topic, type, true);
	}

	@Override
	public Map<String, NavigableMap<Long, QpxData>> getQpxForAllConsumerId(
			String topic, StatisType type, boolean includeTotal) {
		
		return getAllQpx(type, topic, includeTotal);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(
			String topic, StatisType type, boolean includeTotal) {
		
		return getAllDelay(type, topic, includeTotal);
	}

	@Override
	public Set<String> getConsumerIds(String topic, boolean includeTotal) {

		Set<String> topics;
		for(ConsumerServerStatisData csd : servers.values()){
			if(csd != null){
				topics =  csd.keySet(false);
				if(topics != null && topics.contains(topic)){
					ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) csd.getValue(topic);
					if(ctss != null){
						return ctss.keySet(includeTotal);
					}
				}
			}
		}
		return null;
	}

	@Override
	public Map<String, Set<String>> getAllTopics() {
		
		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		Set<String> topics = getTopics(false);
		for(String topic : topics){
			if(topic.equals(MonitorData.TOTAL_KEY)){
				continue;
			}
			Set<String> consumerIds = getConsumerIds(topic, false);
			consumerIds.remove(MonitorData.TOTAL_KEY);
			result.put(topic, consumerIds);
		}
		return result;
	}

	@Override
	public ConsumerServerStatisData createValue() {
		return new ConsumerServerStatisData();
	}

}
