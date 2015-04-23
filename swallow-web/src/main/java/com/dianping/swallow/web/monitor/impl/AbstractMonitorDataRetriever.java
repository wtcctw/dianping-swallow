package com.dianping.swallow.web.monitor.impl;


import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.MonitorData;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitor;
import com.dianping.swallow.web.manager.impl.CacheManager;
import com.dianping.swallow.web.monitor.MonitorDataRetriever;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:30
 */
public abstract class AbstractMonitorDataRetriever implements MonitorDataRetriever{
	
	
	@Value("${swallow.web.monitor.keepinmemory}")
	public int keepInMemoryHour = 2;//保存最后2小时
	
	public static int keepInMemoryCount;

	@Autowired
	private CacheManager cacheManager;

	private Map<String, SwallowServerData> serverMap = new ConcurrentHashMap<String, SwallowServerData>();
	
	
	@PostConstruct
	public void postAbstractMonitorDataStats(){
		
		keepInMemoryCount = keepInMemoryHour * 3600 / AbstractCollector.SEND_INTERVAL;
	}

	/**
	 * 以发送消息的时间间隔为间隔，进行时间对齐
	 * @param currentTime
	 * @return
	 */
	protected static Long getCeilingTime(long currentTime) {
		
		return currentTime/1000/AbstractCollector.SEND_INTERVAL;
	}


	protected boolean dataExistInMemory(long start, long end) {
		
		long oldest = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(keepInMemoryHour, TimeUnit.HOURS);
		
		//允许10s内的误差
		if(oldest <= (start + 10*1000)){
			return true;
		}
		return false;
	}
	
	
	protected void visit(MonitorVisitor monitorVisitor,
			NavigableMap<Long, MonitorData> data) {
		
		for(Entry<Long, MonitorData> entry : data.entrySet()){
			
			MonitorData value = entry.getValue();
			
			value.accept(monitorVisitor);
		}
		
	}
	
	protected NavigableMap<Long, MonitorData> getData(String topic, long start, long end) {
		
		if(dataExistInMemory(start, end)){
			return getMemoryData(topic, start, end);
		}else{
			return retrieveDbData(topic, start, end);
			
		}
	}

	protected NavigableMap<Long, MonitorData> retrieveDbData(String topic,
			long start, long end) {
		
		//wait to be implemented
		return getMemoryData(topic, start, end);
	}


	protected NavigableMap<Long, MonitorData> getMemoryData(String topic, long start, long end) {
		
		SwallowServerData ret = createSwallowServerData();
		
		for(SwallowServerData swallowServerData : serverMap.values()){
			ret.merge(swallowServerData, topic, start, end);
		}
		
		return ret.getMonitorData();
	}

	
	protected abstract SwallowServerData createSwallowServerData();

	@Override
	public void add(MonitorData monitorData) {
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		SwallowServerData serverData = MapUtil.getOrCreate(serverMap, monitorData.getSwallowServerIp(), (Class)getServerDataClass());
		serverData.add(monitorData);
	}


	protected abstract Class<? extends SwallowServerData> getServerDataClass();


	public abstract static class SwallowServerData{
		
		
		private NavigableMap<Long, MonitorData> datas = new TreeMap<Long, MonitorData>();   

		private AtomicInteger count = new AtomicInteger();
		
		public SwallowServerData(){
			
		}
	
		public void merge(SwallowServerData swallowServerData, String topic, long start, long end) {

			for(Entry<Long, MonitorData> entry : swallowServerData.getMonitorData().entrySet()){
				
				Long key = entry.getKey();
				MonitorData value = entry.getValue();
				if(!shouldMerge(value.getCurrentTime(), start, end)){
					continue;
				}
				@SuppressWarnings({ "unchecked", "rawtypes" })
				MonitorData data = MapUtil.getOrCreate(datas, key, (Class)getMonitorDataClass());
				data.merge(topic, value);
			}
			
		}

		protected abstract Class<? extends MonitorData> getMonitorDataClass();

		private boolean shouldMerge(Long dataTime, long start, long end) {
			
			if(dataTime >= start && dataTime <= end){
				return true;
			}
			return false;
		}

		public void add(MonitorData monitorData){
			
			datas.put(getCeilingTime(monitorData.getCurrentTime()), monitorData);
			if(count.incrementAndGet() > keepInMemoryCount){
				datas.pollFirstEntry();
				count.decrementAndGet();
			}
		}
		
		public NavigableMap<Long, MonitorData> getMonitorData(){
			return datas;
		}
	}

	@Override
	public int getKeepInMemoryHour() {
		return keepInMemoryHour;
	}

	public void setKeepInMemoryHour(int keepInMemoryHour) {
		this.keepInMemoryHour = keepInMemoryHour;
	}

}
