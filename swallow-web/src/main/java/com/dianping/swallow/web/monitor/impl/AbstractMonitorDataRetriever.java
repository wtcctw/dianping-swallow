package com.dianping.swallow.web.monitor.impl;


import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.MonitorData;
import com.dianping.swallow.common.server.monitor.visitor.QPX;
import com.dianping.swallow.common.server.monitor.visitor.Visitor;
import com.dianping.swallow.common.server.monitor.visitor.impl.AbstractMonitorVisitor;
import com.dianping.swallow.web.manager.impl.CacheManager;
import com.dianping.swallow.web.monitor.MonitorDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:30
 */
public abstract class AbstractMonitorDataRetriever implements MonitorDataRetriever{
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	
	private final int DEFAULT_INTERVAL_IN_HOUR = 10;//一小时每个10秒采样

	@Value("${swallow.web.monitor.keepinmemory}")
	public int keepInMemoryHour = 1;//保存最后2小时
	
	public static int keepInMemoryCount;

	@Autowired
	private CacheManager cacheManager;

	private Map<String, SwallowServerData> serverMap = new ConcurrentHashMap<String, SwallowServerData>();
	
	protected Set<String> getServerIps(long start, long end) {
		if(dataExistInMemory(start, end)){
			return getServerIpsInMemory(start, end);
		}
		return getServerIpsInDb(start, end);
	}

	private Set<String> getServerIpsInDb(long start, long end) {
		
		return getServerIpsInDb(start, end);
	}

	private Set<String> getServerIpsInMemory(long start, long end) {
		return serverMap.keySet();
	}
	
	@PostConstruct
	public void postAbstractMonitorDataStats(){
		
		keepInMemoryCount = keepInMemoryHour * 3600 / AbstractCollector.SEND_INTERVAL;
	}

	protected long getRealStartTime(NavigableMap<Long, MonitorData> data, long start, long end) {
		try{
			return data.firstKey().longValue()*AbstractCollector.SEND_INTERVAL*1000;
		}catch(NoSuchElementException e){
			if(logger.isInfoEnabled()){
				logger.info("[getRealStartTime][no element, end instead]" + DateUtils.toPrettyFormat(end));
			}
			return end;
		}
	}

	protected int getRealIntervalSeconds(int intervalTimeSeconds, QPX qpx) {
		
		return AbstractMonitorVisitor.getRealIntervalTimeSeconds(intervalTimeSeconds, qpx);
	}

	protected int getRealIntervalSeconds(int intervalTimeSeconds) {
		
		return AbstractMonitorVisitor.getRealIntervalTimeSeconds(intervalTimeSeconds);
	}

	protected StatsData createStatsData(StatsDataDesc info, List<Long> qpsData, long start, long end, NavigableMap<Long, MonitorData> data, int intervalTimeSeconds, QPX qpx) {
		
		return new StatsData(info, qpsData, getRealStartTime(data, start, end), getRealIntervalSeconds(intervalTimeSeconds, qpx));
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
	
	public Set<String> getTopics(){
		
		return getTopics(getDefaultStart(), getDefaultEnd());
	}	
	
	protected long getDefaultEnd() {
		
		return System.currentTimeMillis();
	}

	protected long getDefaultStart() {
		return System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(keepInMemoryHour, TimeUnit.HOURS);
	}

	protected int getDefaultInterval(){
		return keepInMemoryHour * DEFAULT_INTERVAL_IN_HOUR;
	}
	
	@Override
	public Set<String>  getTopics(long start, long end){
		
		if(dataExistInMemory(start, end)){
			getTopicsInMemory(start, end);
		}
		
		return getTopicsInDb(start, end);
	}

	private Set<String> getTopicsInMemory(long start, long end) {
		
		Set<String> topics = new HashSet<String>();
		
		for(SwallowServerData swallowServerData : serverMap.values()){
			topics.addAll(swallowServerData.getTopics());
		}
		return topics;
	}

	private Set<String> getTopicsInDb(long start, long end) {
		
		//TODO
		return getTopicsInMemory(start, end);
	}

	protected void visit(Visitor monitorVisitor,
			NavigableMap<Long, MonitorData> data) {
		
		for(Entry<Long, MonitorData> entry : data.entrySet()){
			
			MonitorData value = entry.getValue();
			
			value.accept(monitorVisitor);
		}
		
	}


	/**
	 * @param topic
	 * @param start
	 * @param end
	 * @param serverIp null 表示所有ip
	 * @return
	 */
	protected NavigableMap<Long, MonitorData> getData(String topic, long start, long end, String serverIp) {

		NavigableMap<Long, MonitorData>  result;
		
		if(dataExistInMemory(start, end)){
			result = getMemoryData(topic, start, end, serverIp);
		}else{
			result = retrieveDbData(topic, start, end, serverIp);
		}
		//插值补齐 
		insertLackedData(result);
		return result;

	}

	protected NavigableMap<Long, MonitorData> getData(String topic, long start, long end) {
		return getData(topic, start, end, null);
	}

	private void insertLackedData(NavigableMap<Long, MonitorData> result) {
		
		long before = 0;
		
		List<Long> toInsert = new LinkedList<Long>();
		for(Entry<Long, MonitorData> entry : result.entrySet()){
			
			long current = entry.getKey();
			if(before >0 && (current - before > 1)){
				if(logger.isInfoEnabled()){
					logger.info("[insertLackedData]" + before + "," + current);
				}
				for(long insert= before + 1; insert < current; insert++){
					toInsert.add(insert);
				}
			}
			before = current;
		}
		
		for(Long insert : toInsert){
			result.put(insert, createMonitorData());
		}
		
	}

	protected abstract MonitorData createMonitorData();

	protected NavigableMap<Long, MonitorData> retrieveDbData(String topic,
			long start, long end, String serverIp) {
		
		//wait to be implemented
		return getMemoryData(topic, start, end, serverIp);
	}


	protected NavigableMap<Long, MonitorData> getMemoryData(String topic, long start, long end, String serverIp) {
		
		SwallowServerData ret = createSwallowServerData();
		
		for(Entry<String, SwallowServerData> entry : serverMap.entrySet()){
			
			String ip = entry.getKey();
			SwallowServerData swallowServerData = entry.getValue();
			
			if(serverIp == null || serverIp.equals(ip)){
				ret.merge(swallowServerData, topic, start, end);
			}
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


	public static abstract class SwallowServerData{
		
		
		private NavigableMap<Long, MonitorData> datas = new TreeMap<Long, MonitorData>();   

		private AtomicInteger count = new AtomicInteger();
		
		public SwallowServerData(){
			
		}
	
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void merge(SwallowServerData swallowServerData, String topic, long start, long end) {

			synchronized (swallowServerData.getMonitorData()) {
				for(Entry<Long, MonitorData> entry : swallowServerData.getMonitorData().entrySet()){
					
					Long key = entry.getKey();
					MonitorData value = entry.getValue();
					if(!shouldMerge(value.getCurrentTime(), start, end)){
						continue;
					}
					
					MonitorData data = null;
					synchronized(datas){
						 data = MapUtil.getOrCreate(datas, key, (Class)getMonitorDataClass());
					}
					data.merge(topic, value);
				}
			}
		}
		
		public Set<String> getTopics() {
			
			Set<String> topics = new HashSet<String>();
			synchronized (datas) {
				for(MonitorData monitorData : datas.values()){
					topics.addAll(monitorData.getTopics());
				}
			}
			return topics;
		}

		private boolean shouldMerge(Long dataTime, long start, long end) {
			
			if(dataTime >= start && dataTime <= end){
				return true;
			}
			return false;
		}

		public void add(MonitorData monitorData){
			
			synchronized (datas) {
				datas.put(getCeilingTime(monitorData.getCurrentTime()), monitorData);
			}
			if(count.incrementAndGet() > keepInMemoryCount){
				datas.pollFirstEntry();
				count.decrementAndGet();
			}
		}

		protected abstract Class<? extends MonitorData> getMonitorDataClass();
		
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
