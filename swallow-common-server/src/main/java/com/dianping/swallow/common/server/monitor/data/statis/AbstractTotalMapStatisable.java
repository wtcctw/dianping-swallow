package com.dianping.swallow.common.server.monitor.data.statis;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.MapStatisable;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午2:09:12
 */
public abstract class AbstractTotalMapStatisable<M extends Mergeable,V extends TotalMap<M>> extends AbstractStatisable<V> implements MapStatisable<V>{

	
	protected Map<String, Statisable<M>> map = new ConcurrentHashMap<String, Statisable<M>>();

	public Set<String> keySet(){
		
		Set<String> result = new HashSet<String>(map.keySet());
		result.remove(MonitorData.TOTAL_KEY);
		return result;
	}
	
	@Override
	public void add(Long time, V added) {

		for(Entry<String, M> entry : added.entrySet()){
			
			String addKey = entry.getKey();
			M addValue = entry.getValue();
			
			Statisable<M> realValue = MapUtil.getOrCreate(map, addKey, getStatisClass());
			realValue.add(time, addValue);
		}
	}

	protected Statisable<?> getValue(Object key){
		return map.get(key); 
	}


	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount, int step) {
		
		for(Entry<String, Statisable<M>> entry : map.entrySet()){
			
			String key = entry.getKey();
			Statisable<M> value = entry.getValue();
			
			if(logger.isDebugEnabled()){
				logger.debug("[build]"+ getStepDebug(step) + key);
			}
			if(logger.isDebugEnabled()){
				logger.debug("[build]" + value);
			}
			value.build(qpx, startKey, endKey, intervalCount, step+1);
			if(logger.isDebugEnabled()){
				if(value instanceof MessageInfoStatis){
					logger.debug("[build]" + getStepDebug(step) + value);
				}
			}
		}
		
	}

	protected String getStepDebug(int step) {
		
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<=step;i++){
			sb.append("-----:");
		}
		return sb.toString();
	}

	@Override
	public void clean() {
		
		for(String key : map.keySet()){
			
			Statisable<M> value = map.get(key);
			value.clean();
			
			if(value.isEmpty()){
				if(logger.isInfoEnabled()){
					logger.info("[clean]" + key);
				}
				map.remove(key);
			}
		}
	}

	@Override
	public boolean isEmpty() {
		
		for(Statisable<M> value : map.values()){
			if(!value.isEmpty()){
				return false;
			}
		}
		
		return true;
	}

	@Override
	public void removeBefore(Long time) {
		
		for(Statisable<M> value : map.values()){
			value.removeBefore(time);
		}
	}

	protected abstract Class<? extends Statisable<M>> getStatisClass();


	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type, Object key) {
		
		Statisable<?> value = getValue(key);
		if(value == null){
			return null;
		}
		return value.getDelay(type);
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type, Object key) {
		
		Statisable<?> value = getValue(key);
		if(value == null){
			return null;
		}
		return value.getQpx(type);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> allDelay(StatisType type){
		
		Map<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long,Long>>();
		for(Entry<String, Statisable<M>> entry : map.entrySet()){
			
			
			String key = entry.getKey();
			
			if(isTotalKey(key)){
				continue;
			}
			
			Statisable<M> value = entry.getValue();
			
			result.put(key, value.getDelay(type));
		}
		return result;
	}
	
	protected boolean isTotalKey(String key) {
		return key.equals(MonitorData.TOTAL_KEY);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> allQpx(StatisType type){

		Map<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long,Long>>();
		for(Entry<String, Statisable<M>> entry : map.entrySet()){
			
			String key = entry.getKey();
			if(isTotalKey(key)){
				continue;
			}
			Statisable<M> value = entry.getValue();
			
			result.put(key, value.getQpx(type));
		}
		return result;
		
	}
	
	@Override
	public NavigableMap<Long, Long> getDelay(StatisType type) {
		
		return getDelay(type, MonitorData.TOTAL_KEY);
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type) {
		
		return getQpx(type, MonitorData.TOTAL_KEY);
	}


}
