package com.dianping.swallow.web.model.event;

public class ServerEvent extends Event {

	private String ip;

	private String slaveIp;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getSlaveIp() {
		return slaveIp;
	}

	public void setSlaveIp(String slaveIp) {
		this.slaveIp = slaveIp;
	}

}
