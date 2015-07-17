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

import com.dianping.swallow.web.controller.dto.ConsumerIdAlarmSettingDto;
import com.dianping.swallow.web.controller.mapper.ConsumerIdAlarmSettingMapper;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 * 2015年7月13日下午5:39:45
 */
@Controller
public class ConsumerIdAlarmSettingController extends AbstractSidebarBasedController {

	@Resource(name = "consumerIdAlarmSettingService")
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	@RequestMapping(value = "/console/setting")
	public ModelAndView mainSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "swallow";
		return new ModelAndView("setting/producerserversetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerid")
	public ModelAndView consumerIdSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "consumerid";
		return new ModelAndView("setting/consumeridsetting", createViewMap());
	}

	@RequestMapping(value = "/console/setting/consumerid/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object comsumeridSettingList(int offset, int limit, HttpServletRequest request, HttpServletResponse response) {

		List<ConsumerIdAlarmSetting> consumerIdAlarmSettingList = consumerIdAlarmSettingService.findByPage(offset, limit);
		List<ConsumerIdAlarmSettingDto> consumerAlarmSettingListDto = new ArrayList<ConsumerIdAlarmSettingDto>();
		for(ConsumerIdAlarmSetting consumerIdAlarmSetting : consumerIdAlarmSettingList){
			consumerAlarmSettingListDto.add(ConsumerIdAlarmSettingMapper.toConsumerIdAlarmSettingDto(consumerIdAlarmSetting));
		}
		return generateResponst(consumerAlarmSettingListDto);
		
	}

	@RequestMapping(value = "/console/setting/consumerid/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void comsumeridSettingCreate(@RequestBody ConsumerIdAlarmSettingDto dto) {

		ConsumerIdAlarmSetting consumerIdAlarmSetting = ConsumerIdAlarmSettingMapper.toConsumerIdAlarmSetting(dto);
		consumerIdAlarmSettingService.update(consumerIdAlarmSetting);
	}

	@RequestMapping(value = "/console/setting/consumerid/remove", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public int remvoeComsumeridSettingCreate(@RequestParam(value = "cid") String cid) {
		
		int result = consumerIdAlarmSettingService.deleteByConsumerId(cid);
		if(result > 0){
			return ResponseStatus.SUCCESS.getStatus();
		}else{
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	private Map<String, Object> generateResponst(List<ConsumerIdAlarmSettingDto> consumerIdAlarmSetting){
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("size", consumerIdAlarmSetting.size());
		map.put("message", consumerIdAlarmSetting);
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

	private String subSide = "consumerid";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
