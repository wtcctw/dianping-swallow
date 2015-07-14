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

import com.dianping.swallow.web.controller.dto.TopicAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.TopicAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.TopicAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日上午10:40:07
 */
@Controller
public class TopicAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "topicAlarmSettingService")
	private TopicAlarmSettingService topicAlarmSettingService;


	@RequestMapping(value = "/console/setting/topic")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "topic";
		return new ModelAndView("setting/topicsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/topic/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {
		
		List<TopicAlarmSetting> topicAlarmSettingList = topicAlarmSettingService.findAll();
		List<TopicAlarmSettingDto> topicAlarmSettingDto = new ArrayList<TopicAlarmSettingDto>();
		for(TopicAlarmSetting topicAlarmSetting : topicAlarmSettingList){
			topicAlarmSettingDto.add(TopicAlarmSettingMapper.toTopicAlarmSettingDto(topicAlarmSetting));
		}
		return generateResponst(topicAlarmSettingDto);
		
	}

	@RequestMapping(value = "/console/setting/topic/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int topicSettingCreate(@RequestBody TopicAlarmSettingDto dto) {

		TopicAlarmSetting topicAlarmSetting = TopicAlarmSettingMapper.toTopicAlarmSetting(dto);
		boolean result = topicAlarmSettingService.update(topicAlarmSetting);
		if(!result){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/setting/topic/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeTopicSettingCreate(@RequestParam(value = "topic") String topic) {
		
		int result = topicAlarmSettingService.deleteByTopicName(topic);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	private Map<String, Object> generateResponst(List<TopicAlarmSettingDto> topicAlarmSettingList){
		
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

	private String subSide = "topic";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
