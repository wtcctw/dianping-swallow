package com.dianping.swallow.web.monitor.impl;


import java.util.List;
import java.util.Map.Entry;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractTotalMapStatisable;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.utils.MonitorUtils;
import com.dianping.swallow.common.server.monitor.visitor.Visitor;
import com.dianping.swallow.web.monitor.MonitorDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 上午11:04:30
 */
public abstract class AbstractMonitorDataRetriever<M extends Mergeable, T extends TotalMap<M>, S extends AbstractTotalMapStatisable<M, T>, V extends MonitorData> implements MonitorDataRetriever{
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	private final int DEFAULT_INTERVAL = 30;//每隔多少秒采样

	@Value("${swallow.web.monitor.keepinmemory}")
	public int keepInMemoryHour = 3;//保存最新小时
	
	protected AbstractAllData<M, T, S, V> statis; 
	
	public static int keepInMemoryCount;
	
	protected ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT);
	
	private  long lastBuildTime = System.currentTimeMillis();
	private  int intervalCount;

	@PostConstruct
	public void postAbstractMonitorDataStats(){
		
		keepInMemoryCount = keepInMemoryHour * 3600 / AbstractCollector.SEND_INTERVAL;
		intervalCount = DEFAULT_INTERVAL/AbstractCollector.SEND_INTERVAL;
		
		statis = createServerStatis();
		startStatisBuilder();
	}

	private void startStatisBuilder() {
		
		scheduled.scheduleWithFixedDelay(new Runnable(){

			@Override
			public void run() {
				
				long current = System.currentTimeMillis();
				try{
					statis.build(QPX.SECOND, getKey(lastBuildTime), getKey(current), intervalCount);
					
					long key = getKey(current - keepInMemoryHour*3600000L);
					if(logger.isInfoEnabled()){
						logger.info("[run][remove]" + key + "," + getKey(current) + "," + keepInMemoryHour);
					}
					statis.removeBefore(key);
				}catch(Throwable th){
					logger.error("[startStatisBuilder]", th);
				}finally{
					lastBuildTime = current;
				}
				
				
			}

		}, DEFAULT_INTERVAL, DEFAULT_INTERVAL, TimeUnit.SECONDS);
	}

	private Long getKey(long timeMili) {
		
		return timeMili/AbstractCollector.SEND_INTERVAL/1000;
	}
	
	protected abstract AbstractAllData<M, T, S, V> createServerStatis();

	protected StatsData getQpxInDb(String topic, StatisType type,
			long start, long end) {
		
		return getQpxInMemory(topic, type, start, end);
	}

	protected Map<String, StatsData> getServerQpxInDb(QPX qpx, StatisType save, long start, long end) {
		
		return getServerQpxInMemory(qpx, save, start, end);
	}

	protected StatsData getDelayInDb(String topic, StatisType type, long start, long end) {
		
		return getDelayInMemory(topic, type, start, end);
	}


	protected StatsData getDelayInMemory(String topic, StatisType type, long start, long end) {
		
		NavigableMap<Long, Long> rawData = statis.getDelayForTopic(topic, type);
		
		StatsData result = new StatsData(createDelayDesc(topic, type), getValue(rawData), getStartTime(rawData, start, end), getDefaultInterval());
		return result;
	}


	protected StatsData getQpxInMemory(String topic, StatisType type, long start, long end) {
		
		NavigableMap<Long, Long> rawData = statis.getQpxForTopic(topic, type);
		StatsData result = new StatsData(createQpxDesc(topic, type), getValue(rawData), getStartTime(rawData, start, end), getDefaultInterval());
		return result;
	}
	
	protected Map<String, StatsData> getServerQpxInMemory(QPX qpx, StatisType type, long start, long end) {
		
		Map<String, StatsData> result = new HashMap<String, StatsData>();
		
		Map<String, NavigableMap<Long, Long>>  serversQpx = statis.getQpxForServers(type);
		
		for(Entry<String, NavigableMap<Long, Long>> entry : serversQpx.entrySet()){
			
			String serverIp = entry.getKey();
			NavigableMap<Long, Long> serverQpx = entry.getValue();
			result.put(serverIp, new StatsData(createServerQpxDesc(serverIp, type), getValue(serverQpx), getStartTime(serverQpx, start, end), getDefaultInterval()));
			
		}
		
		return result;
	}

	protected abstract StatsDataDesc createServerQpxDesc(String serverIp, StatisType type);

	protected abstract StatsDataDesc createServerDelayDesc(String serverIp, StatisType type);

	protected abstract StatsDataDesc createDelayDesc(String topic, StatisType type);
	
	protected abstract StatsDataDesc createQpxDesc(String topic, StatisType type);

	
	protected long getStartTime(NavigableMap<Long, Long> rawData, long start, long end) {

		if(rawData == null){
			return end;
		}
		try{
			return rawData.firstKey().longValue()*AbstractCollector.SEND_INTERVAL*1000;
		}catch(NoSuchElementException e){
			if(logger.isInfoEnabled()){
				logger.info("[getRealStartTime][no element, end instead]" + DateUtils.toPrettyFormat(end));
			}
			return end;
		}
	}

	protected List<Long> getValue(NavigableMap<Long, Long> rawData) {
		
		List<Long> result = new LinkedList<Long>();
		
		if(rawData != null){
			for(Long value : rawData.values()){
				result.add(value);
			}
		}
		return result;
	}

	protected int getRealIntervalSeconds(int intervalTimeSeconds, QPX qpx) {
		
		return MonitorUtils.getRealIntervalTimeSeconds(intervalTimeSeconds, qpx);
	}

	protected int getRealIntervalSeconds(int intervalTimeSeconds) {
		
		return MonitorUtils.getRealIntervalTimeSeconds(intervalTimeSeconds);
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
		return DEFAULT_INTERVAL;
	}
	
	@Override
	public Set<String>  getTopics(long start, long end){
		
		if(dataExistInMemory(start, end)){
			getTopicsInMemory(start, end);
		}
		
		return getTopicsInDb(start, end);
	}

	private Set<String> getTopicsInMemory(long start, long end) {
		
		return statis.getTopics(true);
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

	@SuppressWarnings("unchecked")
	@Override
	public void add(MonitorData monitorData) {
		
		statis.add(monitorData.getKey(), (V) monitorData);
	}


	@Override
	public int getKeepInMemoryHour() {
		return keepInMemoryHour;
	}

	public void setKeepInMemoryHour(int keepInMemoryHour) {
		this.keepInMemoryHour = keepInMemoryHour;
	}

}
