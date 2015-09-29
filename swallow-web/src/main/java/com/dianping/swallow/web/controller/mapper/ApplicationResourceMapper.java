package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.ApplicationResourceDto;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.ApplicationResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:57:07
 */
public class ApplicationResourceMapper {

	public static ApplicationResource toApplicationResource(ApplicationResourceDto dto) {
		
		ApplicationResource applicationResource = new ApplicationResource();
		
		IPDesc iPDesc = new IPDesc();
		iPDesc.setCreateTime(dto.getCreateTime());
		iPDesc.setDpManager(dto.getDpManager());
		iPDesc.setDpMobile(dto.getDpMobile());
		iPDesc.setEmail(dto.getEmail());
		iPDesc.setName(dto.getApplication());
		iPDesc.setOpEmail(dto.getOpEmail());
		iPDesc.setOpManager(dto.getOpManager());
		iPDesc.setOpMobile(dto.getOpMobile());
		
		applicationResource.setiPDesc(iPDesc);
		applicationResource.setId(dto.getId());
		applicationResource.setApplication(dto.getApplication());

		return applicationResource;
		
	}
	
	public static ApplicationResourceDto toApplicationResourceDto(ApplicationResource applicationResource) {
		
		ApplicationResourceDto dto = new ApplicationResourceDto();
		
		IPDesc iPDesc = applicationResource.getiPDesc();
		
		dto.setCreateTime(iPDesc.getCreateTime());
		dto.setDpManager(iPDesc.getDpManager());
		dto.setDpMobile(iPDesc.getDpMobile());
		dto.setEmail(iPDesc.getEmail());
		dto.setApplication(iPDesc.getName());
		dto.setOpEmail(iPDesc.getOpEmail());
		dto.setOpManager(iPDesc.getOpManager());
		dto.setOpMobile(iPDesc.getOpMobile());
		
		dto.setId(applicationResource.getId());
		dto.setApplication(applicationResource.getApplication());
		
		return dto;
		
	}
}
