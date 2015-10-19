package com.dianping.swallow.web.dashboard.model;


/**
 * @author mingdongli
 *
 * 2015年8月13日下午5:47:12
 */
public enum DashboardEnum {

	COMPREHENSIVE("comprehensive"),SEND("senddelay"),ACK("ackdelay"),ACCU("accu");
	
	private String type;
	
	DashboardEnum(String type){
		
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static DashboardEnum findByType(String type) {
		for (DashboardEnum dashboard : values()) {
			if (type.equals(dashboard.getType())) {
				return dashboard;
			}
		}
		throw new RuntimeException("Error type : " + type);
	}
	
}
