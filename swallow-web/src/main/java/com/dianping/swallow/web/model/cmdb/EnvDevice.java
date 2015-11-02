package com.dianping.swallow.web.model.cmdb;

/**
 * 
 * @author qiyin
 *
 *         2015年10月16日 上午10:09:24
 */
public class EnvDevice {

	private String hostName;

	private String ip;

	private String env;

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getEnv() {
		return env;
	}

	public void setEnv(String env) {
		this.env = env;
	}

	public String toString() {
		return "EnvDevice [ hostName = " + hostName + ", ip = " + ip + ", env = " + env + " ]";
	}
}
