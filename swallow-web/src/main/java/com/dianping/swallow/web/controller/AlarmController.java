package com.dianping.swallow.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.util.DateUtil;

@Controller
public class AlarmController extends AbstractSidebarBasedController {

	@Autowired
	private AlarmService alarmService;

	@RequestMapping(value = "/console/tool")
	public ModelAndView alarm(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("tool/alarmquery", createViewMap());
	}

	@RequestMapping(value = "/console/alarm/search", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object alarmSearch(@RequestParam("receiver") String receiver, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam("offset") int offset,
			@RequestParam("limit") int limit) {

		Date startDate = null;
		if (StringUtils.isNotBlank(startTime)) {
			startDate = DateUtil.convertStrToDate(startTime);
		}
		Date endDate = null;
		if (StringUtils.isNotBlank(endTime)) {
			endDate = DateUtil.convertStrToDate(endTime);
		} else {
			endDate = new Date();
		}
		List<Alarm> alarms = alarmService.findByReceiverAndTime(receiver, startDate, endDate, offset, limit);
		long count = alarmService.countByReceiverAndTime(receiver, startDate, endDate);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("size", count);
		result.put("entitys", alarms);
		return result;
	}

	@Override
	protected String getMenu() {
		return "tool";
	}

	@Override
	protected String getSide() {
		
		return "tool";
	}
	
	private static String subSide = "alarmming";

	@Override
	public String getSubSide() {
		
		return subSide;
	}

}
