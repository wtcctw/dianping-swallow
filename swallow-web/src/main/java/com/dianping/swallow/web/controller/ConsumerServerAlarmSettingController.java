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

import com.dianping.swallow.web.controller.dto.ConsumerServerAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.ConsumerServerAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日上午10:40:07
 */
@Controller
public class ConsumerServerAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "consumerServerAlarmSettingService")
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService;


	@RequestMapping(value = "/console/setting/consumerserver")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "consumerserver";
		return new ModelAndView("setting/consumerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerserver/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object consumerserverSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<ConsumerServerAlarmSetting> consumerAlarmSettingList = consumerServerAlarmSettingService.findAll();
		List<ConsumerServerAlarmSettingDto> consumerAlarmSettingListDto = new ArrayList<ConsumerServerAlarmSettingDto>();
		for(ConsumerServerAlarmSetting consumerAlarmSetting : consumerAlarmSettingList){
			consumerAlarmSettingListDto.add(ConsumerServerAlarmSettingMapper.toConsumerServerAlarmSettingDto(consumerAlarmSetting));
		}
		return generateResponst(consumerAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/consumerserver/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int consumerserverSettingCreate(@RequestBody ConsumerServerAlarmSettingDto dto) {

		ConsumerServerAlarmSetting consumerServerAlarmSetting = ConsumerServerAlarmSettingMapper.toConsumerServerAlarmSetting(dto);
		boolean result = consumerServerAlarmSettingService.update(consumerServerAlarmSetting);
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/consumerserver/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeConsumerserverSettingCreate(@RequestParam(value = "serverId") String serverId) {
		
		int result = consumerServerAlarmSettingService.deleteByServerId(serverId);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	private Map<String, Object> generateResponst(List<ConsumerServerAlarmSettingDto> topicAlarmSettingList){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("size", topicAlarmSettingList.size());
		map.put("message", topicAlarmSettingList);
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

	private String subSide = "consumerserver";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
