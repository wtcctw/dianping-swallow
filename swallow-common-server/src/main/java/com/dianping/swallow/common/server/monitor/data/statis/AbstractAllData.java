package com.dianping.swallow.common.server.monitor.data.statis;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 服务器相关，存储server对应的监控数据
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:45:31
 */
public abstract class AbstractAllData<M extends Mergeable, T extends TotalMap<M>, S extends AbstractTotalMapStatisable<M, T>, V extends MonitorData> 
														extends AbstractStatisable<V> implements StatisRetriever{

	protected Map<String, S> servers = new ConcurrentHashMap<String, S>();
	
	@JsonIgnore
	protected   S 			 total = null;
	
	protected final Set<StatisType> 	supportedTypes = new HashSet<StatisType>();
	
	public AbstractAllData(StatisType ... types){
		
		for(StatisType type : types){
			supportedTypes.add(type);
		}
		total = MapUtil.getOrCreate(servers, MonitorData.TOTAL_KEY, getStatisClass());
	}

	
	@Override
	public Set<String> getTopics(boolean includeTotal){
		return total.keySet(includeTotal);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void add(Long time, V added) {
		
		String serverIp = added.getSwallowServerIp();
		
		S statis = MapUtil.getOrCreate(servers, serverIp, getStatisClass());
		
		statis.add(time, (T) added.getServerData());
		
		total.add(time, (T) added.getServerData());
	}

	protected abstract Class<? extends S> getStatisClass();

	@Override
	public void doRemoveBefore(Long time) {
		
		for(S s : servers.values()){
			s.removeBefore(time);
		}
	}
	
	
	@Override
	protected Statisable<?> getValue(Object key){
		return servers.get(key);
	}
	
	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount) {
		
		for(Entry<String, S> entry : servers.entrySet()){
			
			String key = entry.getKey();
			S 	   value =  entry.getValue();
			if(logger.isDebugEnabled()){
				logger.debug("[build][" +startKey + "," + endKey + "," + intervalCount + "]" + key + ","+ value);
			}
			value.build(qpx, startKey, endKey, intervalCount);
		}
		
	}

	@Override
	public void cleanEmpty() {
		for(Entry<String, S> entry : servers.entrySet()){
			
			String key = entry.getKey();
			S 	   value = entry.getValue();
			
			value.cleanEmpty();
			if(!isTotalKey(key) && value.isEmpty()){
				if(logger.isInfoEnabled()){
					logger.info("[clean]" + key);
				}
				servers.remove(key);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		
		for(S s : servers.values()){
			if(!s.isEmpty()){
				return false;
			}
		}
		return true;
	}

	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type) {
		
		checkSupported(type);
		return getDelayForTopic(MonitorData.TOTAL_KEY, type);
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type) {
		
		checkSupported(type);
		return getQpxForTopic(MonitorData.TOTAL_KEY, type);
	}

	
	@Override
	public NavigableMap<Long, Long> getQpxForTopic(String topic, StatisType type) {
		
		checkSupported(type);
		
		Statisable<?> statis = total.getValue(topic);
		if(statis != null){
			return statis.getQpx(type);
		}
		return null;
	}

	private void checkSupported(StatisType type) {
		if(!supportedTypes.contains(type)){
			throw new IllegalArgumentException("unsupported type:" + type + ", class:" + getClass());
		}
	}


	@Override
	public NavigableMap<Long, Long> getDelayForTopic(String topic, StatisType type) {
		
		checkSupported(type);
		
		Statisable<?> statis = total.getValue(topic);
		if(statis != null){
			return statis.getDelay(type);
		}
		return null;
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> getQpxForServers(StatisType type) {
		
		checkSupported(type);
		HashMap<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long, Long>>();
		for(Entry<String, S> entry : servers.entrySet()){
			
			String serverIp = entry.getKey();
			S pssd = entry.getValue();
			if(pssd == total){
				continue;
			}
			result.put(serverIp, pssd.getQpx(type));
		}
		
		return result;
	}

	
	protected Map<String, NavigableMap<Long, Long>> getAllQpx(StatisType type, String topic, boolean includeTotal) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		
		if(ctss == null){
			return null;
		}
		
		return ctss.allQpx(type, includeTotal);
	}

	protected Map<String, NavigableMap<Long, Long>> getAllDelay(StatisType type, String topic, boolean includeTotal) {
		
		ConsumerTopicStatisData ctss = (ConsumerTopicStatisData) total.getValue(topic);
		
		if(ctss == null){
			return null;
		}
		
		return ctss.allDelay(type, includeTotal);
	}
	
}
