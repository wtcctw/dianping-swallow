package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.AlarmSearchDto;
import com.dianping.swallow.web.controller.mapper.AlarmMapper;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月9日 下午5:11:52
 */
@Controller
public class AlarmController extends AbstractSidebarBasedController {

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@RequestMapping(value = "/console/tool")
	public ModelAndView alarm(HttpServletRequest request, HttpServletResponse response) {
		return new ModelAndView("tool/alarmquery", createViewMap());
	}

	@RequestMapping(value = "/console/alarm/search", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object alarmSearch(@RequestBody AlarmSearchDto alarmSearchDto) {

		Date startDate = null;
		if (StringUtils.isNotBlank(alarmSearchDto.getStartTime())) {
			startDate = DateUtil.convertStrToDate(alarmSearchDto.getStartTime());
		}
		Date endDate = null;
		if (StringUtils.isNotBlank(alarmSearchDto.getEndTime())) {
			endDate = DateUtil.convertStrToDate(alarmSearchDto.getEndTime());
		} else {
			endDate = new Date();
		}
		Pair<List<Alarm>, Long> results = null;
		if (alarmSearchDto.getRelatedType().isConsumerId() && StringUtils.isNotBlank(alarmSearchDto.getRelatedInfo())) {
			String relateds[] = alarmSearchDto.getRelatedInfo().split(" ");
			if (relateds.length == 2) {
				results = alarmService.findByPage(alarmSearchDto.getReceiver(), relateds[1], relateds[0], startDate,
						endDate, alarmSearchDto.getOffset(), alarmSearchDto.getLimit());
			}
		} else {
			results = alarmService.findByPage(alarmSearchDto.getReceiver(), alarmSearchDto.getRelatedInfo(), startDate,
					endDate, alarmSearchDto.getOffset(), alarmSearchDto.getLimit());
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (results != null) {
			result.put("size", results.getSecond());
			result.put("entitys", AlarmMapper.getAlarmDtos(results.getFirst()));
		}
		return result;
	}

	@RequestMapping(value = "/console/alarm/detail/{eventId}", method = RequestMethod.GET)
	public ModelAndView alarmDetail(@PathVariable long eventId) {
		Map<String, Object> paras = super.createViewMap();
		paras.put("entity", alarmService.findByEventId(eventId));
		return new ModelAndView("tool/alarmdetail", paras);

	}

	@RequestMapping(value = "/console/alarm/query/ip", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object searchServerIps() {
		List<String> serverIps = new ArrayList<String>();
		serverIps.addAll(ipCollectorService.getConsumerServerMasterIps());
		serverIps.addAll(ipCollectorService.getConsumerServerSlaveIps());
		serverIps.addAll(ipCollectorService.getProducerServerIps());
		return serverIps;
	}

	@RequestMapping(value = "/console/alarm/query/consumerid", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object searchConsumerIds() {
		return consumerStatsDataWapper.getConusmerIdInfos();
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
