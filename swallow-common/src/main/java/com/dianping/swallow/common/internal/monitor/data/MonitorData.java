package com.dianping.swallow.common.internal.monitor.data;


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
	
}
