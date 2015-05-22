package com.dianping.swallow.common.server.monitor.data.statis;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.dianping.swallow.common.server.monitor.data.ProducerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:46:41
 */
public class ProducerAllData extends AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> 
						implements ProducerStatisRetriever{

	@Override
	protected Class<? extends ProducerServerStatisData> getStatisClass() {
		
		return ProducerServerStatisData.class;
	}

	@Override
	public NavigableMap<Long, Long> getSaveQpxForTopic(String topic) {
		
		return getTopicQpx(StatisType.SAVE, topic);
	}

	@Override
	public NavigableMap<Long, Long> getSaveDelayForTopic(String topic) {
		
		return getTopicDelay(StatisType.SAVE, topic);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getSaveQpxForServers() {
		
		HashMap<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long, Long>>();
		for(Entry<String, ProducerServerStatisData> entry : servers.entrySet()){
			
			String serverIp = entry.getKey();
			ProducerServerStatisData pssd = entry.getValue();
			if(pssd == total){
				continue;
			}
			result.put(serverIp, pssd.getQpx(StatisType.SAVE));
		}
		
		return result;
	}

	@Override
	public NavigableMap<Long, Long> getSaveQpxForServer(String serverIp) {
		
		ProducerServerStatisData pssd = servers.get(serverIp);
		if(pssd == null){
			return null;
		}
		
		return pssd.getQpx(StatisType.SAVE);
	}
	
}
