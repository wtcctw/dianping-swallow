package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.dto.SwallowAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.SwallowAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.SwallowAlarmSetting;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class SwallowAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "swallowAlarmSettingService")
	private SwallowAlarmSettingService swallowAlarmSettingService;


	@RequestMapping(value = "/console/setting/swallow")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("setting/swallowsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/swallow/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object producerserverSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<SwallowAlarmSetting> swallowAlarmSettingList = swallowAlarmSettingService.findByPage(offset, limit);
		List<SwallowAlarmSettingDto> swallowAlarmSettingListDto = new ArrayList<SwallowAlarmSettingDto>();
		for(SwallowAlarmSetting swallowAlarmSetting : swallowAlarmSettingList){
			swallowAlarmSettingListDto.add(SwallowAlarmSettingMapper.toSwallowAlarmSettingDto(swallowAlarmSetting));
		}
		return generateResponse(swallowAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/swallow/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int producerserverSettingCreate(@RequestBody SwallowAlarmSettingDto dto) {

		SwallowAlarmSetting consumerServerAlarmSetting = SwallowAlarmSettingMapper.toSwallowAlarmSetting(dto);
		boolean result = swallowAlarmSettingService.update(consumerServerAlarmSetting);
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/swallow/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeProducerserverSettingCreate(@RequestParam(value = "swallowId") String swallowId) {
		
		int result = swallowAlarmSettingService.deleteByBySwallowId(swallowId);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	private Map<String, Object> generateResponse(List<SwallowAlarmSettingDto> swallowAlarmSettingList){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("size", swallowAlarmSettingList.size());
		map.put("message", swallowAlarmSettingList);
		return map;
	}
	
	@Override
	protected String getMenu() {
		return "setting";
	}

	@Override
	protected String getSide() {
		return "warn";
	}

	private String subSide = "swallow";

	@Override
	public String getSubSide() {
		return subSide;
	}


}
