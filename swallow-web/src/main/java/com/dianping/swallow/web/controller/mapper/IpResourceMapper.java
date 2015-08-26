package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.IpResourceDto;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.IpResource;

public class IpResourceMapper {

	public static IpResource toIpResource(IpResourceDto dto) {
		
		IpResource ipResource = new IpResource();
		
		IPDesc iPDesc = new IPDesc();
		iPDesc.setCreateTime(dto.getCreateTime());
		iPDesc.setDpManager(dto.getDpManager());
		iPDesc.setDpMobile(dto.getDpMobile());
		iPDesc.setEmail(dto.getEmail());
		iPDesc.setIp(dto.getIp());
		iPDesc.setName(dto.getName());
		iPDesc.setOpEmail(dto.getOpEmail());
		iPDesc.setOpManager(dto.getOpManager());
		iPDesc.setOpMobile(dto.getOpMobile());
		
		ipResource.setiPDesc(iPDesc);
		ipResource.setId(dto.getId());
		ipResource.setAlarm(dto.isAlarm());
		ipResource.setIp(dto.getIp());
		ipResource.setIpType(dto.getIpType());

		return ipResource;
		
	}
	
	public static IpResourceDto toIpResourceDto(IpResource ipResource) {
		
		IpResourceDto dto = new IpResourceDto();
		
		IPDesc iPDesc = ipResource.getiPDesc();
		
		dto.setCreateTime(iPDesc.getCreateTime());
		dto.setDpManager(iPDesc.getDpManager());
		dto.setDpMobile(iPDesc.getDpMobile());
		dto.setEmail(iPDesc.getEmail());
		dto.setIp(iPDesc.getIp());
		dto.setName(iPDesc.getName());
		dto.setOpEmail(iPDesc.getOpEmail());
		dto.setOpManager(iPDesc.getOpManager());
		dto.setOpMobile(iPDesc.getOpMobile());
		
		dto.setId(ipResource.getId());
		dto.setAlarm(ipResource.isAlarm());
		dto.setIp(ipResource.getIp());
		dto.setIpType(ipResource.getIpType());
		
		return dto;
		
	}
}

