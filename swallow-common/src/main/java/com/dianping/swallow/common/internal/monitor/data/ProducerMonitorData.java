package com.dianping.swallow.common.internal.monitor.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午3:29:56
 */
public class ProducerMonitorData extends MonitorData{

	private Map<String, TopicInfo> all = new ConcurrentHashMap<String, TopicInfo>(); 
	
	
	//for json deserialize
	public ProducerMonitorData(){
		
	}
	
	public ProducerMonitorData(String swallowServerIp) {
		super(swallowServerIp);
	}

	public void addData(String topic, long messageId, long sendTime, long saveTime){

		TopicInfo topicInfo = getTopicInfo(topic);
		
		topicInfo.increaseNum();
		topicInfo.increaseDelay(saveTime - sendTime);
		
	}

	
	private TopicInfo getTopicInfo(String topic) {
		
		TopicInfo topicInfo = null;
		
		synchronized (topic.intern()) {
			
			topicInfo = all.get(topic);
			if(topicInfo == null){
				topicInfo = new TopicInfo();
				all.put(topic, topicInfo);
			}
		}
		return topicInfo;
	}

	public static class TopicInfo{
				
		private AtomicLong totalDelay = new AtomicLong();

		private AtomicLong total = new AtomicLong();

		public void increaseNum(){
			total.incrementAndGet();
		}
		
		public void increaseDelay(long delay){
			totalDelay.addAndGet(delay);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof TopicInfo)){
				return false;
			}
			TopicInfo cmp = (TopicInfo) obj;
			
			return cmp.totalDelay.get() == totalDelay.get() 
					&& cmp.total.get() == total.get();
		}
		
		@Override
		public int hashCode() {
			return (int) (totalDelay.get() ^ total.get());
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!super.equals(obj)){
			return false;
		}
		if(!(obj instanceof ProducerMonitorData)){
			return false;
		}
		
		ProducerMonitorData cmp = (ProducerMonitorData) obj;
		
		return cmp.all.equals(all);
	}
	
	@Override
	public int hashCode() {
		int hash = 0;
		
		hash = hash*31 + super.hashCode();
		hash = hash*31 + all.hashCode();
		return hash;
	}
}
