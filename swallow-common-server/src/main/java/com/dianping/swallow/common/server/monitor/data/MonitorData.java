package com.dianping.swallow.common.server.monitor.data;


import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.common.internal.codec.JsonBinder;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.visitor.Acceptable;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitor;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:44:46
 */
public abstract class MonitorData implements KeyMergeable, Acceptable{
	
	
	public static final String TOTAL_KEY = "total";
	
	
	@Transient
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	@Indexed
	private String swallowServerIp;

	private long currentTime;

	public MonitorData(){
		
	}
	
	public MonitorData(String swallowServerIp){
		this.swallowServerIp = swallowServerIp;
	}
	
	@Override
	public void merge(Mergeable merge) {
		
		checkTypeMatch(merge);
		
		doMerge((MonitorData) merge);
	}
	
	private void checkTypeMatch(Mergeable merge) {
		
		if(merge == null || !(getClass().isAssignableFrom(merge.getClass()))){
			throw new IllegalArgumentException("wrong type " + merge);
		}
		
	}

	public void merge(String topic, KeyMergeable merge){
		
		checkTypeMatch(merge);
		
		Mergeable toMergeData = getTopic(merge, topic);
		
		if(toMergeData == null){
			logger.warn("[doTopMerge][no topic]" + toMergeData);
			return;
		}
		
		Mergeable self = getTopic(topic);
		self.merge(toMergeData);
	}

	protected abstract Mergeable getTopic(KeyMergeable merge, String topic);

	protected abstract Mergeable getTopic(String topic);
	
	protected abstract void doMerge(MonitorData mergeData);

	public String jsonSerialize(){
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		setCurrentTime(System.currentTimeMillis());
		return jsonBinder.toJson(this);
		
	}

	public static <T> T jsonDeSerialize(String jsonData, Class<T> clazz){
		
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		return jsonBinder.fromJson(jsonData, clazz);
	}

	
	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}


	public String getSwallowServerIp() {
		return swallowServerIp;
	}

	public void setSwallowServerIp(String swallowServerIp) {
		this.swallowServerIp = swallowServerIp;
	}

	@Override
	public boolean equals(Object obj) {

		if(!(obj instanceof MonitorData)){
			return false;
		}
		
		MonitorData cmp = (MonitorData) obj;
		
		return cmp.currentTime == this.currentTime 
				&& cmp.swallowServerIp.equals(this.swallowServerIp);
	}
	
	@Override
	public int hashCode() {
		
		int hash = swallowServerIp!=null ? swallowServerIp.hashCode(): 0; 
		hash = (int) (hash*31 + currentTime);
		return hash;
	}

	
	public static class MessageInfo implements Mergeable{
		
		private AtomicLong totalDelay = new AtomicLong();

		private AtomicLong total = new AtomicLong();
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof MessageInfo)){
				return false;
			}
			MessageInfo cmp = (MessageInfo) obj;
			
			return cmp.totalDelay.get() == totalDelay.get() 
					&& cmp.total.get() == total.get();
		}
		
		@Override
		public int hashCode() {
			return (int) (totalDelay.get() ^ total.get());
		}

		public void addMessage(long messageId, long startTime, long endTime) {
			total.incrementAndGet();
			totalDelay.addAndGet(endTime - startTime);
		}

		public long getTotalDelay() {
			return totalDelay.get();
		}

		public long getTotal() {
			return total.get();
		}
		
		@Override
		public void merge(Mergeable merge) {
			if(!(merge instanceof MessageInfo)){
				throw new IllegalArgumentException("wrong type " + merge.getClass());
			}
			MessageInfo toMerge = (MessageInfo) merge;
			total.addAndGet(toMerge.total.get());
			totalDelay.addAndGet(toMerge.totalDelay.get());
		}
		
		@Override
		public String toString() {
			return JsonBinder.getNonEmptyBinder().toJson(this);
		}
	}
	
	
	@Override
	public void accept(MonitorVisitor visitor){
		
		String topic = visitor.getVisitTopic();
		visitor.visitTopic(getTopicData(topic));
	}


	protected abstract TotalMap<?> getTopicData(String topic);
	
	@Override
	public String toString() {
		return jsonSerialize();
	}

}