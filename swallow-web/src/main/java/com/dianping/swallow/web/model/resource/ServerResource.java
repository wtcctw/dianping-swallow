package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午4:58:04
 */
public abstract class ServerResource extends BaseResource{

	private String ip;
	
	private String hostname;
	
	private boolean alarm;
	
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
	
	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

	@Override
	public String toString() {
		return "ServerResource [ip=" + ip + ", hostname=" + hostname + ", alarm=" + alarm + ", toString()="
				+ super.toString() + "]";
	}
	
}
