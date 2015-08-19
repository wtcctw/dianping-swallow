package com.dianping.swallow.web.controller.dto;

import com.dianping.swallow.web.model.resource.IpType;


/**
 * @author mingdongli
 *
 * 2015年8月18日下午8:10:12
 */
public class IpQueryDto extends BaseDto{
	
	private String ip;
	
	private IpType ipType;
	
	public IpQueryDto(){
		
	}
	
	public IpQueryDto(String ip){
		this(ip, null);
	}
	
	public IpQueryDto(IpType ipType){
		this(null, ipType);
	}
	
	public IpQueryDto(String ip, IpType ipType){
		super();
		this.ip = ip;
		this.ipType = ipType;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public IpType getIpType() {
		return ipType;
	}

	public void setIpType(IpType ipType) {
		this.ipType = ipType;
	}
	
}
