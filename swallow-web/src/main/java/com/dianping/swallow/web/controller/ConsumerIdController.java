package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.controller.dto.ConsumerIdResourceDto;
import com.dianping.swallow.web.controller.mapper.ConsumerIdResourceMapper;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年8月27日下午3:33:36
 */
@Controller
public class ConsumerIdController extends AbstractMenuController {

	private static final String CONSUMERID = "consumerId";

	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;

	@RequestMapping(value = "/console/consumerid")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("consumerid/index", createViewMap());
	}

	@RequestMapping(value = "/console/consumerid/list", method = RequestMethod.POST)
	@ResponseBody
	public Object comsumeridResourceList(@RequestBody ConsumerIdQueryDto ConsumerIdQueryDto) {

		String topic = ConsumerIdQueryDto.getTopic();
		String consumerId = ConsumerIdQueryDto.getConsumerId();
		Pair<Long, List<ConsumerIdResource>> pair = new Pair<Long, List<ConsumerIdResource>>();
		List<ConsumerIdResource> result = new ArrayList<ConsumerIdResource>();
		List<ConsumerIdResourceDto> resultDto = new ArrayList<ConsumerIdResourceDto>();

		boolean isBlank = StringUtil.isBlank(topic + consumerId);

		if (isBlank) {
			pair = consumerIdResourceService.findConsumerIdResourcePage(ConsumerIdQueryDto);
		} else {
			if (StringUtil.isBlank(topic)) {
				result = consumerIdResourceService.findByConsumerId(consumerId);
				long size = result.size();
				pair.setFirst(size);
				pair.setSecond(result);
			} else if (StringUtil.isBlank(consumerId)) {
				pair = consumerIdResourceService.findByTopic(ConsumerIdQueryDto);
			} else {
				ConsumerIdResource consumerIdResource = consumerIdResourceService.find(topic, consumerId);
				result.add(consumerIdResource);
				pair.setFirst(1L);
				pair.setSecond(result);
			}
		}

		for (ConsumerIdResource consumerIdResource : pair.getSecond()) {
			resultDto.add(ConsumerIdResourceMapper.toConsumerIdResourceDto(consumerIdResource));
		}
		return new Pair<Long, List<ConsumerIdResourceDto>>(pair.getFirst(), resultDto);

	}

	@RequestMapping(value = "/console/consumerid/update", method = RequestMethod.POST)
	@ResponseBody
	public Object updateTopic(@RequestBody ConsumerIdResourceDto consumerIdResourceDto) throws UnknownHostException {

		ConsumerIdResource consumerIdResource = ConsumerIdResourceMapper.toConsumerIdResource(consumerIdResourceDto);
		boolean result = consumerIdResourceService.update(consumerIdResource);

		if (result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/consumerid/alarm", method = RequestMethod.GET)
	@ResponseBody
	public void editProducerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm,
			@RequestParam String consumerId, HttpServletRequest request, HttpServletResponse response) {

		ConsumerIdResource consumerIdResource = consumerIdResourceService.find(topic, consumerId);
		consumerIdResource.setAlarm(alarm);
		boolean result = consumerIdResourceService.update(consumerIdResource);

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update alarm of %s to %b successfully", topic, alarm));
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update alarm of %s to %b fail", topic, alarm));
			}
		}
	}

	@RequestMapping(value = "/console/consumerid/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeComsumerid(@RequestParam(value = "consumerId") String consumerId,
			@RequestParam(value = "topic") String topic) {

		int result = consumerIdResourceService.remove(topic, consumerId);
		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/consumerid/allconsumerid", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadCmsumerid() {

		Set<String> consumerids = new HashSet<String>();
		List<ConsumerIdResource> consumerIdResources = consumerIdResourceService.findAll(CONSUMERID);

		for (ConsumerIdResource consumerIdResource : consumerIdResources) {
			String cid = consumerIdResource.getConsumerId();
			if (!consumerids.contains(cid)) {
				consumerids.add(cid);
			}
		}

		return new ArrayList<String>(consumerids);
	}

	@Override
	protected String getMenu() {
		return "consumerid";
	}
}
