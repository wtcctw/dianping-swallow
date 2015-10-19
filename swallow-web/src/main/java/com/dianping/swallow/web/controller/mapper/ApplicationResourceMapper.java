package com.dianping.swallow.web.controller.mapper;

import com.dianping.swallow.web.controller.dto.ApplicationResourceDto;
import com.dianping.swallow.web.model.resource.ApplicationResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:57:07
 */
public class ApplicationResourceMapper {

	public static ApplicationResource toApplicationResource(ApplicationResourceDto dto) {
		
		ApplicationResource applicationResource = new ApplicationResource();
		
		applicationResource.setDpManager(dto.getDpManager());
		applicationResource.setDpMobile(dto.getDpMobile());
		applicationResource.setEmail(dto.getEmail());
		applicationResource.setOpEmail(dto.getOpEmail());
		applicationResource.setOpManager(dto.getOpManager());
		applicationResource.setOpMobile(dto.getOpMobile());
		applicationResource.setId(dto.getId());
		applicationResource.setApplication(dto.getApplication());

		return applicationResource;
		
	}
	
	public static ApplicationResourceDto toApplicationResourceDto(ApplicationResource applicationResource) {
		
		ApplicationResourceDto dto = new ApplicationResourceDto();
		
		dto.setDpManager(applicationResource.getDpManager());
		dto.setDpMobile(applicationResource.getDpMobile());
		dto.setEmail(applicationResource.getEmail());
		dto.setOpEmail(applicationResource.getOpEmail());
		dto.setOpManager(applicationResource.getOpManager());
		dto.setOpMobile(applicationResource.getOpMobile());
		
		dto.setId(applicationResource.getId());
		dto.setApplication(applicationResource.getApplication());
		
		return dto;
		
	}
}
