package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.IpQueryDto;
import com.dianping.swallow.web.controller.dto.IpResourceDto;
import com.dianping.swallow.web.controller.mapper.IpResourceMapper;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.IpResourceService;

@Controller
public class IPController extends AbstractMenuController{
	
	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;
	
	@RequestMapping(value = "/console/ip")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("ip/index", createViewMap());
	}
	
	@RequestMapping(value = "/console/ip/list", method = RequestMethod.POST)
	@ResponseBody
	public Object producerserverSettingList(@RequestBody IpQueryDto ipQueryDto) {

		String ip = ipQueryDto.getIp();
		String ipType = ipQueryDto.getIpType();
		
		Pair<Long, List<IpResource>> pair = null;
		
		Boolean isAllBlank = StringUtil.isAllBlank(ip, ipType);
		if(isAllBlank){
			pair = ipResourceService.findIpResourcePage(ipQueryDto);
		}else{
			if(StringUtil.isBlank(ipType)){
				List<IpResource> ipResources = ipResourceService.findByIp(ip);
				long size = ipResources.size();
				pair = new  Pair<Long, List<IpResource>>(size, ipResources);
			}else if(StringUtil.isBlank(ipType)){
				pair = ipResourceService.findByIpType(ipQueryDto);
			}else{
				pair = ipResourceService.find(ipQueryDto);
			}
		}
		
		List<IpResourceDto> ipResourceDto = new ArrayList<IpResourceDto>();
		for (IpResource ipResource : pair.getSecond()) {
			ipResourceDto.add(IpResourceMapper.toIpResourceDto(ipResource));
		}
		return new Pair<Long, List<IpResourceDto>>(pair.getFirst(),
				ipResourceDto);

	}
	
	@Override
	protected String getMenu() {
		return "ip";
	}

}

