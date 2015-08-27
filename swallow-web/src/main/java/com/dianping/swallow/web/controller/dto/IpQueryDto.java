package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月18日下午8:10:12
 */
public class IpQueryDto extends BaseDto{
	
	private String ip;
	
	private String ipType;
	
	public IpQueryDto(){
		
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getIpType() {
		return ipType;
	}

	public void setIpType(String ipType) {
		this.ipType = ipType;
	}
	
}
