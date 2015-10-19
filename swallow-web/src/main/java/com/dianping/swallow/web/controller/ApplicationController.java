package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ApplicationQueryDto;
import com.dianping.swallow.web.controller.dto.ApplicationResourceDto;
import com.dianping.swallow.web.controller.mapper.ApplicationResourceMapper;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.service.ApplicationResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mingdongli
 *
 *         2015年9月29日下午1:41:02
 */
@Controller
public class ApplicationController extends AbstractMenuController {

	@Resource(name = "applicationResourceService")
	private ApplicationResourceService applicationResourceService;
	
	@Autowired
	private UserUtils userUtils;

	@RequestMapping(value = "/console/application")
	public ModelAndView ipView() {

		return new ModelAndView("application/index", createViewMap());
	}

	@RequestMapping(value = "/console/application/list", method = RequestMethod.POST)
	@ResponseBody
	public Object applicationList(@RequestBody ApplicationQueryDto applicationQueryDto) {

		int offset = applicationQueryDto.getOffset();
		int limit = applicationQueryDto.getLimit();
		String application = applicationQueryDto.getApplication();
		Pair<Long, List<ApplicationResource>> pair;

		if (StringUtils.isNotBlank(application)) {
			String[] applications = application.trim().split(",");
			pair = applicationResourceService.find(offset, limit, applications);
		} else {
			pair = applicationResourceService.findApplicationResourcePage(offset, limit);
		}
		List<ApplicationResourceDto> applicationResourceDto = new ArrayList<ApplicationResourceDto>();
		for (ApplicationResource applicationResource : pair.getSecond()) {
			applicationResourceDto.add(ApplicationResourceMapper.toApplicationResourceDto(applicationResource));
		}
		return new Pair<Long, List<ApplicationResourceDto>>(pair.getFirst(), applicationResourceDto);

	}
	
	@RequestMapping(value = "/console/application/create", method = RequestMethod.POST)
	@ResponseBody
	public int applicationResourceCreate(@RequestBody ApplicationResourceDto dto) {

		ApplicationResource applicationResource = ApplicationResourceMapper.toApplicationResource(dto);
		boolean result = applicationResourceService.update(applicationResource);

		if (!result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@RequestMapping(value = "/console/application/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeApplicationResource(@RequestParam(value = "application") String application) {

		int result = applicationResourceService.remove(application);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@RequestMapping(value = "/console/application/applicationname", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadApplication() {

		return userUtils.allApplications();
	}

	@Override
	protected String getMenu() {

		return "application";
	}

}
