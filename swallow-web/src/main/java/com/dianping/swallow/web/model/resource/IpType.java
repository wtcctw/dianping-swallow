package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:52:22
 */
public enum IpType {
	
	PRODUCERIP("生产者"),
	CONSUMERIP("消费者");
	
	private String type;
	
	private IpType(){
	}
	
	private IpType(String type){
		this.type = type;
		
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return type;
	}
}
