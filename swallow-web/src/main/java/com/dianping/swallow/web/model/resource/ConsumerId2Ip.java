package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年8月13日上午11:00:09
 */
public class ConsumerId2Ip extends BaseResource{
	
	private String consumerId;
	
	private String ip;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	@Override
	public String toString() {
		return "ConsumerId2Ip [consumerId=" + consumerId + ", ip=" + ip + ", toString()=" + super.toString() + "]";
	}

}
