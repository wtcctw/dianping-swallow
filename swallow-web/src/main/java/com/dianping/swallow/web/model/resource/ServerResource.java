package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午4:58:04
 */
public class ServerResource extends BaseResource {

	@Indexed(name = "IX_IP", direction = IndexDirection.DESCENDING)
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

	@JsonIgnore
	public boolean isDefault() {
		if (DEFAULT_RECORD.equals(ip)) {
			return true;
		}
		return false;
	}

}
