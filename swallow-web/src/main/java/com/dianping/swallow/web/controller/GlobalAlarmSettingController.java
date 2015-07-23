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

import com.dianping.swallow.web.controller.dto.GlobalAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.GlobalAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class GlobalAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "globalAlarmSettingService")
	private GlobalAlarmSettingService globalAlarmSettingService;


	@RequestMapping(value = "/console/setting/swallow")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("setting/swallowsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/swallow/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object producerserverSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<GlobalAlarmSetting> swallowAlarmSettingList = globalAlarmSettingService.findByPage(offset, limit);
		List<GlobalAlarmSettingDto> swallowAlarmSettingListDto = new ArrayList<GlobalAlarmSettingDto>();
		for(GlobalAlarmSetting swallowAlarmSetting : swallowAlarmSettingList){
			swallowAlarmSettingListDto.add(GlobalAlarmSettingMapper.toSwallowAlarmSettingDto(swallowAlarmSetting));
		}
		return generateResponse(swallowAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/swallow/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int producerserverSettingCreate(@RequestBody GlobalAlarmSettingDto dto) {

		GlobalAlarmSetting consumerServerAlarmSetting = GlobalAlarmSettingMapper.toSwallowAlarmSetting(dto);
		boolean result = globalAlarmSettingService.update(consumerServerAlarmSetting);
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/swallow/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeProducerserverSettingCreate(@RequestParam(value = "swallowId") String swallowId) {
		
		int result = globalAlarmSettingService.deleteByBySwallowId(swallowId);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	private Map<String, Object> generateResponse(List<GlobalAlarmSettingDto> swallowAlarmSettingList){
		
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
