package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午3:36:34
 */
@Document(collection = "IP_RESOURCE")
@CompoundIndexes({ @CompoundIndex(name = "IX_APPLICATION_IP", def = "{'application': -1, 'ip': -1}" , unique = true, dropDups = true) })
public class IpResource extends BaseResource {

	private String ip;

	private String application;

	private boolean alarm;

	public IpResource(String ip, String application, boolean alarm) {
		this.ip = ip;
		this.application = application;
		this.alarm = alarm;
	}

	public IpResource() {

	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
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

	@Override
	public String toString() {
		return "IpResource [ip=" + ip + ", alarm=" + alarm + "]";
	}

	@Override
	public boolean isDefault() {
		return false;
	}

}
