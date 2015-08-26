package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.ServerResourceDto;
import com.dianping.swallow.web.controller.mapper.ProducerServerResourceMapper;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.service.UserService;

@Controller
public class IPController extends AbstractMenuController{
	
	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;
	
	@RequestMapping(value = "/console/ip")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("ip/index", createViewMap());
	}
	
//	@RequestMapping(value = "/console/ip/producer/list", method = RequestMethod.POST)
//	@ResponseBody
//	public Object producerserverSettingList(@RequestBody BaseDto baseDto) {
//
//		Pair<Long, List<ProducerServerResource>> pair = producerServerResourceService
//				.findProducerServerResourcePage(baseDto);
//		List<ServerResourceDto> producerServerResourceDto = new ArrayList<ServerResourceDto>();
//		for (ProducerServerResource producerServerResource : pair.getSecond()) {
//			producerServerResourceDto.add(ProducerServerResourceMapper
//					.toProducerServerResourceDto(producerServerResource));
//		}
//		return new Pair<Long, List<ServerResourceDto>>(pair.getFirst(),
//				producerServerResourceDto);
//
//	}
	
	@Override
	protected String getMenu() {
		return "ip";
	}

}

