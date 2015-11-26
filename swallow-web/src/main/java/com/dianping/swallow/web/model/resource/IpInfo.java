package com.dianping.swallow.web.model.resource;

/**
 * @author mingdongli
 *
 *         2015年9月25日下午5:05:47
 */
public class IpInfo {

	private String ip;

	private boolean alarm;

	private boolean active;

	public IpInfo() {

	}

	public IpInfo(String ip, boolean alarm, boolean active) {
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

	public boolean isActiveAndAlarm() {
		return active && alarm;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		IpInfo ipInfo = (IpInfo) o;

		if (alarm != ipInfo.alarm) return false;
		if (active != ipInfo.active) return false;
		return !(ip != null ? !ip.equals(ipInfo.ip) : ipInfo.ip != null);

	}

	@Override
	public int hashCode() {
		int result = ip != null ? ip.hashCode() : 0;
		result = 31 * result + (alarm ? 1 : 0);
		result = 31 * result + (active ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		return "IpInfo [ip=" + ip + ", alarm=" + alarm + ", active=" + active + "]";
	}

}
