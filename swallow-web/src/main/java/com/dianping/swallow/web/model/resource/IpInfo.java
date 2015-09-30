package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年9月25日下午5:05:47
 */
public class IpInfo {

	private String ip;
	
	private boolean alarm;
	
	private boolean active;
	
	public IpInfo(){
		
	}

	public IpInfo(String ip, boolean alarm, boolean active){
		this.ip = ip;
		this.alarm = alarm;
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}
	
	public boolean isActiveAndAlarm(){
		return active && alarm;
	}
	
}
