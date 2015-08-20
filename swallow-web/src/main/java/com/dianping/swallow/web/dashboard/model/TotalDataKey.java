package com.dianping.swallow.web.dashboard.model;


/**
 * @author mingdongli
 *
 * 2015年8月15日上午11:08:25
 */
public class TotalDataKey implements Comparable<TotalDataKey>{
	
	private String topic;
	
	private String consumerId;
	
	public TotalDataKey(){
		
	}
	
	public TotalDataKey(String topic, String consumerId){
		
		this.topic = topic;
		this.consumerId = consumerId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	@Override
	public String toString() {
		return "TotalDataKey [topic=" + topic + ", consumerId=" + consumerId + "]";
	}

	@Override
	public int compareTo(TotalDataKey key) {

		return (this.topic + this.consumerId).compareToIgnoreCase(key.topic + key.consumerId);
	}
	
}
