package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.ConsumerServerResourceDto;
import com.dianping.swallow.web.controller.dto.ServerResourceDto;
import com.dianping.swallow.web.controller.mapper.ConsumerServerResourceMapper;
import com.dianping.swallow.web.controller.mapper.ProducerServerResourceMapper;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class ServerController extends AbstractSidebarBasedController {

	@Resource(name = "producerServerResourceService")
	private ProducerServerResourceService producerServerResourceService;
	
	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	@Resource(name = "ipCollectorService")
	private IPCollectorService ipCollectorService;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@RequestMapping(value = "/console/server")
	public ModelAndView serverSetting(HttpServletRequest request,
			HttpServletResponse response) {

		return new ModelAndView("server/producer", createViewMap());
	}

	@RequestMapping(value = "/console/server/producer")
	public ModelAndView producerServerSetting(HttpServletRequest request,
			HttpServletResponse response) {

		subSide = "producer";
		return new ModelAndView("server/producer", createViewMap());
	}
	
	@RequestMapping(value = "/console/server/producer/list", method = RequestMethod.POST)
	@ResponseBody
	public Object producerserverSettingList(@RequestBody BaseDto baseDto) {

		Pair<Long, List<ProducerServerResource>> pair = producerServerResourceService
				.findProducerServerResourcePage(baseDto);
		List<ServerResourceDto> producerServerResourceDto = new ArrayList<ServerResourceDto>();
		for (ProducerServerResource producerServerResource : pair.getSecond()) {
			producerServerResourceDto.add(ProducerServerResourceMapper
					.toProducerServerResourceDto(producerServerResource));
		}
		return new Pair<Long, List<ServerResourceDto>>(pair.getFirst(),
				producerServerResourceDto);

	}

	@RequestMapping(value = "/console/server/producer/create", method = RequestMethod.POST)
	@ResponseBody
	public int producerServerResourceCreate(
			@RequestBody ServerResourceDto dto) {

		ProducerServerResource producerServerResource = ProducerServerResourceMapper
				.toProducerServerResource(dto);
		boolean result = producerServerResourceService
				.update(producerServerResource);

		if (!result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/producer/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeProducerserverResource(
			@RequestParam(value = "serverId") String serverId) {

		int result = producerServerResourceService.remove(serverId);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/producerserverids", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadProducerSereverIds() {

		Set<String> hostNames = ipCollectorService.getProducerServerIpsMap()
				.keySet();
		return new ArrayList<String>(hostNames);
	}

	@RequestMapping(value = "/console/server/producertopics", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadProducerSereverTopics(
			@RequestParam(value = "serverId") String serverId) {

		Set<String> topics = consumerDataRetrieverWrapper.getKey(serverId);

		if (topics != null) {
			return new ArrayList<String>(topics);
		} else {
			return new ArrayList<String>();
		}
	}
	
	@RequestMapping(value = "/console/server/consumer")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "consumer";
		return new ModelAndView("server/consumer", createViewMap());
	}
	
	@RequestMapping(value = "/console/server/consumer/list", method = RequestMethod.POST)
	@ResponseBody
	public Object consumerserverSettingList(@RequestBody BaseDto baseDto) {

		Pair<Long, List<ConsumerServerResource>> pair = consumerServerResourceService.findConsumerServerResourcePage(baseDto);
		List<ConsumerServerResourceDto> consumerServerResourceDto = new ArrayList<ConsumerServerResourceDto>();
		for (ConsumerServerResource consumerServerResource : pair.getSecond()) {
			consumerServerResourceDto.add(ConsumerServerResourceMapper.toConsumerServerResourceDto(consumerServerResource));
		}
		return new Pair<Long, List<ConsumerServerResourceDto>>(pair.getFirst(),
				consumerServerResourceDto);

	}
	
	@RequestMapping(value = "/console/server/consumer/create", method = RequestMethod.POST)
	@ResponseBody
	public int ConsumerServerResourceCreate(
			@RequestBody ConsumerServerResourceDto dto) {

		ConsumerServerResource consumerServerResource = ConsumerServerResourceMapper.toConsumerResourceSetting(dto);
		boolean result = consumerServerResourceService.update(consumerServerResource);

		if (!result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@RequestMapping(value = "/console/server/consumer/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeConsumerserverResource(
			@RequestParam(value = "serverId") String serverId) {

		int result = consumerServerResourceService.remove(serverId);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/console/server/consumerserverids", method = RequestMethod.GET)
	@ResponseBody
	public Collection<String>  loadConsumerSereverIds() {

		Set<String> masterIps =  ipCollectorService.getConsumerServerMasterIpsMap().keySet();
		Set<String> slaveIps =  ipCollectorService.getConsumerServerMasterIpsMap().keySet();
		return CollectionUtils.union(masterIps, slaveIps);
	}

	@Override
	protected String getMenu() {
		return "server";
	}

	@Override
	protected String getSide() {
		return "serverwarn";
	}

	private String subSide = "producer";

	@Override
	public String getSubSide() {
		return subSide;
	}

}
