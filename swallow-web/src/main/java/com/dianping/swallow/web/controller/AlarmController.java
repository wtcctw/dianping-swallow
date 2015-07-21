package com.dianping.swallow.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.service.AlarmService;

@Controller
public class AlarmController extends AbstractMenuController {

	@Autowired
	private AlarmService alarmService;

	@RequestMapping(value = "/console/alarm")
	public ModelAndView alarm(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("alarm/index", createViewMap());
	}

	@RequestMapping(value = "/console/alarm/search", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object alarmSearch(@RequestParam("receiver") String receiver, @RequestParam("startTime") String startTime,
			@RequestParam("endTime") String endTime, @RequestParam("offset") int offset,
			@RequestParam("limit") int limit) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = null;
		Date endDate = null;
		try {
			startDate = sdf.parse(startTime);
			endDate = sdf.parse(endTime);
		} catch (ParseException e) {
			logger.info("data tranform failed.", e);
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
		return "alarm";
	}

}
