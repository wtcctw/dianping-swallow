package com.dianping.swallow.web.controller;


import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.IpQueryDto;
import com.dianping.swallow.web.controller.dto.IpResourceDto;
import com.dianping.swallow.web.controller.mapper.IpResourceMapper;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.IpResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.codehaus.plexus.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author mingdongli
 *
 *         2015年8月27日下午3:04:37
 */
@Controller
public class IPController extends AbstractMenuController {

	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;

	@Autowired
	private UserUtils userUtils;

	@RequestMapping(value = "/console/ip")
	public ModelAndView ipView() {

		return new ModelAndView("ip/index", createViewMap());
	}

	@RequestMapping(value = "/console/ip/list", method = RequestMethod.POST)
	@ResponseBody
	public Object ipQuery(@RequestBody IpQueryDto ipQueryDto, HttpServletRequest request) {

		Pair<Long, List<IpResource>> pair;
		List<IpResourceDto> ipResourceDto = new ArrayList<IpResourceDto>();
		String username = userUtils.getUsername(request);
		int offset = ipQueryDto.getOffset();
		int limit = ipQueryDto.getLimit();
		String ip = ipQueryDto.getIp();
		String application = ipQueryDto.getApplication();
		String type = ipQueryDto.getType();
		if (StringUtils.isNotBlank(type)) {
			List<String> ips;

			if ("PRODUCER".equals(type)) {
				ips = userUtils.producerIps(username);
			} else {
				ips = userUtils.consumerIps(username);
			}

			if (StringUtils.isNotBlank(ip)) {
				String[] queryIps = StringUtils.split(ip);

				Collection<String> intersection = CollectionUtils.intersection(ips, Arrays.asList(queryIps));
				if (intersection.isEmpty()) {
					return new Pair<Long, List<IpResourceDto>>(0L, ipResourceDto);
				} else {
					ip = StringUtils.join(intersection, ",");
				}
			} else {
				ip = StringUtils.join(ips, ",");
			}
		}

		Boolean isAllBlank = StringUtils.isBlank(ip) && StringUtils.isBlank(application);

		if (isAllBlank) {
			pair = ipResourceService.findIpResourcePage(offset, limit);
		} else {

			if (StringUtils.isBlank(application)) {
				String[] ips = ip.split(",");
				if (ips.length == 1) {
					if(!userUtils.ips(username).contains(ips[0])){
						return new Pair<Long, List<IpResourceDto>>(0L, ipResourceDto);
					}
				}
				int size = userUtils.ips(username).size();
				int length = ips.length;
				boolean adminIp =  userUtils.isAdministrator(username) && (size == length);
				pair = ipResourceService.findByIp(offset, limit, adminIp, ips);
			} else if (StringUtils.isBlank(ip)) {
				pair = ipResourceService.findByApplication(offset, limit, application);
			} else {
				String[] ips = ip.split(",");
				pair = ipResourceService.find(offset, limit, application, ips);
			}

		}

		for (IpResource ipResource : pair.getSecond()) {
			ipResourceDto.add(IpResourceMapper.toIpResourceDto(ipResource));
		}

		return new Pair<Long, List<IpResourceDto>>(pair.getFirst(), ipResourceDto);

	}

	@RequestMapping(value = "/console/ip/update", method = RequestMethod.POST)
	@ResponseBody
	public Object updateIp(@RequestBody IpResourceDto IpResourceDto) {

		IpResource ipResource = IpResourceMapper.toIpResource(IpResourceDto);
		boolean result = ipResourceService.update(ipResource);

		if (result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/ip/allip", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadConsumerips(HttpServletRequest request) {

		String username = userUtils.getUsername(request);
		return userUtils.ips(username);
	}

	@RequestMapping(value = "/console/ip/application", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadApplication(HttpServletRequest request) {

		String username = userUtils.getUsername(request);
		return userUtils.applications(username);
	}

	@RequestMapping(value = "/console/ip/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editIpAlarm(@RequestParam String ip, @RequestParam boolean alarm) {

		Pair<Long, List<IpResource>> pair = ipResourceService.findByIp(0, 1, false, ip);
		if (pair.getFirst() == 0) {
			throw new RuntimeException(String.format("Record of %s not found.", ip));
		}
		List<IpResource> ipResources = pair.getSecond();
		IpResource ipResource = ipResources.get(0);
		ipResource.setAlarm(alarm);
		return ipResourceService.update(ipResource);
	}

	@Override
	protected String getMenu() {
		return "ip";
	}

}
