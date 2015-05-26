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

	public ConsumerAllData(){
		super(StatisType.SEND, StatisType.ACK);
	}
	
	@Override
	protected Class<? extends ConsumerServerStatisData> getStatisClass() {
		
		return ConsumerServerStatisData.class;
	}


	@Override
	public Set<String> getConsumerIds(String topic) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		if(ctss == null){
			return null;
		}
		
		return ctss.keySet();
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getQpxForAllConsumerId(
			String topic, StatisType type) {
		
		return getAllQpx(type, topic);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getDelayForAllConsumerId(
			String topic, StatisType type) {
		
		return getAllDelay(type, topic);
	}

}
