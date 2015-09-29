package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:36:34
 */
@Document(collection = "IP_RESOURCE")
public class IpResource extends BaseResource{

	@Indexed(name = "IX_IP", direction = IndexDirection.ASCENDING)
	private String ip;
	
	private String application;
	
	private boolean alarm;
	
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
