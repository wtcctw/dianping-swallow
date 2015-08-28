package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.cmdb.IPDesc;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午3:36:34
 */
@Document(collection = "IP_RESOURCE")
public class IpResource extends BaseResource{

	@Indexed
	private String ip;
	
	private boolean alarm;
	
	private IPDesc iPDesc;

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

	public IPDesc getiPDesc() {
		return iPDesc;
	}

	public void setiPDesc(IPDesc iPDesc) {
		this.iPDesc = iPDesc;
	}

	@Override
	public String toString() {
		return "IpResource [ip=" + ip + ", alarm=" + alarm + ", iPDesc=" + iPDesc + ", toString()=" + super.toString()
				+ "]";
	}

}
