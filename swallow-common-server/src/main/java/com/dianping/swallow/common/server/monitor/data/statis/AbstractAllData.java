package com.dianping.swallow.common.server.monitor.data.statis;


import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisRetriever;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.Statisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * 服务器相关，存储server对应的监控数据
 * @author mengwenchao
 *
 * 2015年5月19日 下午4:45:31
 */
public abstract class AbstractAllData<M extends Mergeable, T extends TotalMap<M>, S extends AbstractTotalMapStatisable<M, T>, V extends MonitorData> 
														extends AbstractStatisable<V> implements StatisRetriever{

	protected transient final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected Map<String, S> servers = new ConcurrentHashMap<String, S>();
	protected   S 			 total = null;
	
	protected AbstractAllData(){
		
		total = MapUtil.getOrCreate(servers, MonitorData.TOTAL_KEY, getStatisClass());
	}

	
	@Override
	public Set<String> getTopics(){
		return total.keySet();
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
	public void removeBefore(Long time) {
		
		for(S s : servers.values()){
			s.removeBefore(time);
		}
	}
	
	
	@Override
	protected Statisable<?> getValue(Object key){
		return servers.get(key);
	}
	
	@Override
	public void build(QPX qpx, Long startKey, Long endKey, int intervalCount, int step) {
		
		for(Entry<String, S> entry : servers.entrySet()){
			
			String key = entry.getKey();
			S 	   value =  entry.getValue();
			if(logger.isDebugEnabled()){
				logger.debug("[build]" + key);
			}
			value.build(qpx, startKey, endKey, intervalCount, step);
		}
		
	}

	@Override
	public void clean() {
		for(Entry<String, S> entry : servers.entrySet()){
			
			String key = entry.getKey();
			S 	   value = entry.getValue();
			
			value.clean();
			if(value.isEmpty()){
				if(logger.isInfoEnabled()){
					logger.info("[clean]" + key);
					servers.remove(key);
				}
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
		
		return getTopicDelay(type, MonitorData.TOTAL_KEY);
	}

	@Override
	public NavigableMap<Long, Long> getQpx(StatisType type) {
		
		return getTopicQpx(type, MonitorData.TOTAL_KEY);
	}

	
	protected NavigableMap<Long, Long> getTopicQpx(StatisType type, String topic) {
		
		Statisable<?> statis = total.getValue(topic);
		if(statis != null){
			return statis.getQpx(type);
		}
		return null;
	}

	protected NavigableMap<Long, Long> getTopicDelay(StatisType type, String topic) {
		
		Statisable<?> statis = total.getValue(topic);
		if(statis != null){
			return statis.getDelay(type);
		}
		return null;
	}


}
