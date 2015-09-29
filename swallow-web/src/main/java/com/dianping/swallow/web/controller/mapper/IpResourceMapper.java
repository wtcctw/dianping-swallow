package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.IpResourceDto;
import com.dianping.swallow.web.model.resource.IpResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午3:18:41
 */
public class IpResourceMapper {

	public static IpResource toIpResource(IpResourceDto dto) {
		
		IpResource ipResource = new IpResource();
		
		ipResource.setId(dto.getId());
		ipResource.setAlarm(dto.isAlarm());
		ipResource.setIp(dto.getIp());
		ipResource.setApplication(dto.getApplication());

		return ipResource;
		
	}
	
	public static IpResourceDto toIpResourceDto(IpResource ipResource) {
		
		IpResourceDto dto = new IpResourceDto();
		
		dto.setId(ipResource.getId());
		dto.setAlarm(ipResource.isAlarm());
		dto.setIp(ipResource.getIp());
		dto.setApplication(ipResource.getApplication());
		
		return dto;
		
	}
}

