package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:52:22
 */
public enum IpType {
	
	PRODUCERIP("PRODUCER"),
	CONSUMERIP("CONSUMER");
	
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
	
	public static IpType findByType(String type) {
		for (IpType ipType : values()) {
			if (type.equals(ipType.getType())) {
				return ipType;
			}
		}
		throw new RuntimeException("Error type : " + type);
	}
	
	@Override
	public String toString() {
		return type;
	}
}
