package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月18日下午8:10:12
 */
public class IpQueryDto extends BaseQueryDto{
	
	private String ip;
	
	private String application;
	
	private String type;
	
	public String getIp() {
		return ip;
	}

	public String getApplication() {
		return application;
	}

	public String getType() {
		return type;
	}
	
}
