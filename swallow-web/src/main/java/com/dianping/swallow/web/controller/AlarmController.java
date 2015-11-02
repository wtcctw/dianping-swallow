package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.controller.dto.AlarmSearchDto;
import com.dianping.swallow.web.controller.mapper.AlarmMapper;
import com.dianping.swallow.web.dao.AlarmDao.AlarmParam;
import com.dianping.swallow.web.model.alarm.Alarm;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.util.DateUtil;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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
	private ResourceContainer resourceContainer;

	@RequestMapping(value = "/console/tool")
	public ModelAndView alarm() {
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
		AlarmParam alarmParam = new AlarmParam();
		alarmParam.setStartTime(startDate).setEndTime(endDate).setLimit(alarmSearchDto.getLimit())
				.setOffset(alarmSearchDto.getOffset()).setReceiver(alarmSearchDto.getReceiver());
		if (alarmSearchDto.getRelatedType().isConsumerId() && StringUtils.isNotBlank(alarmSearchDto.getRelatedInfo())) {
			String relateds[] = alarmSearchDto.getRelatedInfo().split(" ");
			if (relateds.length == 2) {
				results = alarmService.findByPage(alarmParam.setRelated(relateds[1]).setSubRelated(relateds[0]));
			}
		} else {
			results = alarmService.findByPage(alarmParam.setRelated(alarmSearchDto.getRelatedInfo()));
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
		Set<String> serverIps = new HashSet<String>();
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		List<ConsumerServerResource> cServerResources = resourceContainer.findConsumerServerResources(false);
		for (ProducerServerResource serverResource : pServerResources) {
			serverIps.add(serverResource.getIp());
		}
		for (ConsumerServerResource serverResource : cServerResources) {
			serverIps.add(serverResource.getIp());
		}
		return serverIps;
	}

	@RequestMapping(value = "/console/alarm/query/consumerid", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object searchConsumerIds() {
		Set<String> consumerIds = new HashSet<String>();
		List<ConsumerIdResource> consumerIdResources = resourceContainer.findConsumerIdResources(false);
		if (consumerIdResources != null) {
			for (ConsumerIdResource consumerIdResource : consumerIdResources) {
				consumerIds.add(consumerIdResource.getConsumerId() + " " + consumerIdResource.getTopic());
			}
		}
		return consumerIds;
	}

	@Override
	protected String getMenu() {
		return "tool";
	}

	@Override
	protected String getSide() {

		return "alarm";
	}

	private static String subSide = "alarmquery";

	@Override
	public String getSubSide() {

		return subSide;
	}

}
