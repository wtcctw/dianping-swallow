package com.dianping.swallow.web.controller.dto;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日下午2:26:06
 */
public class ServerResourceDto extends BaseAlarmResourceDto{
	
	private String ip;
	
	private String hostname;
	
	private boolean active;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}
