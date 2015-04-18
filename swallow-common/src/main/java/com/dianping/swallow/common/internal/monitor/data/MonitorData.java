package com.dianping.swallow.common.internal.monitor.data;


import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:44:46
 */
public abstract class MonitorData {
	
	protected transient final Logger logger = LoggerFactory.getLogger(getClass());

	private String swallowServerIp;

	private long currentTime;

	public MonitorData(){
		
	}
	
	public MonitorData(String swallowServerIp){
		this.swallowServerIp = swallowServerIp;
	}
	

	
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

	
	public static class MessageInfo{
		
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
	}
	
	public static abstract class AbstractMonitorData{
		
		protected MessageInfo getMessageInfo(String ip, Map<String, MessageInfo> messages) {
			
			MessageInfo messageInfo;
			synchronized (ip.intern()) {
				messageInfo = messages.get(ip);
				if(messageInfo == null){
					messageInfo = new MessageInfo();
					messages.put(ip, messageInfo);
				}
			}
			return messageInfo;
		}
	}

	@Override
	public String toString() {
		return jsonSerialize();
	}

}
