package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
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
import com.dianping.swallow.web.controller.dto.MongoResourceDto;
import com.dianping.swallow.web.controller.dto.ServerResourceDto;
import com.dianping.swallow.web.controller.mapper.ConsumerServerResourceMapper;
import com.dianping.swallow.web.controller.mapper.MongoResourceMapper;
import com.dianping.swallow.web.controller.mapper.ProducerServerResourceMapper;
import com.dianping.swallow.web.dao.impl.DefaultMongoDao;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.MongoResourceService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class ServerController extends AbstractSidebarBasedController {

	private static final String DEFAULT = "default";

	@Resource(name = "producerServerResourceService")
	private ProducerServerResourceService producerServerResourceService;

	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	@Resource(name = "mongoResourceService")
	private MongoResourceService mongoResourceService;

	@Resource(name = "ipCollectorService")
	private IPCollectorService ipCollectorService;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@RequestMapping(value = "/console/server")
	public ModelAndView serverSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "producer";
		return new ModelAndView("server/producer", createViewMap());
	}

	@RequestMapping(value = "/console/server/producer")
	public ModelAndView producerServerSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "producer";
		return new ModelAndView("server/producer", createViewMap());
	}

	@RequestMapping(value = "/console/server/producer/list", method = RequestMethod.POST)
	@ResponseBody
	public Object producerserverSettingList(@RequestBody BaseDto baseDto) {

		int offset = baseDto.getOffset();
		int limit = baseDto.getLimit();
		Pair<Long, List<ProducerServerResource>> pair = producerServerResourceService.findProducerServerResourcePage(
				offset, limit);
		List<ServerResourceDto> producerServerResourceDto = new ArrayList<ServerResourceDto>();
		for (ProducerServerResource producerServerResource : pair.getSecond()) {
			producerServerResourceDto.add(ProducerServerResourceMapper
					.toProducerServerResourceDto(producerServerResource));
		}
		return new Pair<Long, List<ServerResourceDto>>(pair.getFirst(), producerServerResourceDto);

	}

	@RequestMapping(value = "/console/server/producer/create", method = RequestMethod.POST)
	@ResponseBody
	public int producerServerResourceCreate(@RequestBody ServerResourceDto dto) {

		ProducerServerResource producerServerResource = ProducerServerResourceMapper.toProducerServerResource(dto);
		boolean result = producerServerResourceService.update(producerServerResource);

		if (!result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/producer/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeProducerserverResource(@RequestParam(value = "serverId") String serverId) {

		int result = producerServerResourceService.remove(serverId);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/producerserverinfo", method = RequestMethod.GET)
	@ResponseBody
	public Object loadProducerSereverIps() {

		List<String> hostsList;
		List<String> ipsList;
		Map<String, String> hostNames = ipCollectorService.getProducerServerIpsMap();

		if (hostNames != null) {
			Set<String> hosts = hostNames.keySet();
			hostsList = new ArrayList<String>(hosts);
			Collection<String> ipCollection = hostNames.values();
			ipsList = new ArrayList<String>(ipCollection);
		} else {
			hostsList = new ArrayList<String>();
			ipsList = new ArrayList<String>();
		}

		return new Pair<List<String>, List<String>>(hostsList, ipsList);
	}

	@RequestMapping(value = "/console/server/producertopics", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadProducerSereverTopics(@RequestParam(value = "serverId") String serverId) {

		Set<String> topics = consumerDataRetrieverWrapper.getKey(serverId);

		if (topics != null) {
			return new ArrayList<String>(topics);
		} else {
			return new ArrayList<String>();
		}
	}

	@RequestMapping(value = "/console/server/defaultpresource", method = RequestMethod.GET)
	@ResponseBody
	public Object loadDefaultProducerSereverResource() {

		ProducerServerResource producerServerResource = (ProducerServerResource) producerServerResourceService
				.findDefault();

		return ProducerServerResourceMapper.toProducerServerResourceDto(producerServerResource);

	}

	@RequestMapping(value = "/console/server/producer/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editProducerAlarmSetting(@RequestParam String ip, @RequestParam boolean alarm,
			HttpServletRequest request, HttpServletResponse response) {

		ProducerServerResource producerServerResource = (ProducerServerResource) producerServerResourceService
				.findByIp(ip);
		producerServerResource.setAlarm(alarm);
		boolean result = producerServerResourceService.update(producerServerResource);

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update producer server alarm of %s to %b successfully", ip, alarm));
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update producer server alarm of %s to %b fail", ip, alarm));
			}
		}
		return result;
	}

	/**
	 * 
	 * Consumer server controller
	 */

	@RequestMapping(value = "/console/server/consumer")
	public ModelAndView topicSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "consumer";
		return new ModelAndView("server/consumer", createViewMap());
	}

	@RequestMapping(value = "/console/server/consumer/list", method = RequestMethod.POST)
	@ResponseBody
	public Object consumerserverSettingList(@RequestBody BaseDto baseDto) {

		int offset = baseDto.getOffset();
		int limit = baseDto.getLimit();
		Pair<Long, List<ConsumerServerResource>> pair = consumerServerResourceService.findConsumerServerResourcePage(
				offset, limit);
		List<ConsumerServerResourceDto> consumerServerResourceDto = new ArrayList<ConsumerServerResourceDto>();
		for (ConsumerServerResource consumerServerResource : pair.getSecond()) {
			consumerServerResourceDto.add(ConsumerServerResourceMapper
					.toConsumerServerResourceDto(consumerServerResource));
		}
		return new Pair<Long, List<ConsumerServerResourceDto>>(pair.getFirst(), consumerServerResourceDto);

	}

	@RequestMapping(value = "/console/server/consumer/create", method = RequestMethod.POST)
	@ResponseBody
	public int ConsumerServerResourceCreate(@RequestBody ConsumerServerResourceDto dto) {

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
	public int remvoeConsumerserverResource(@RequestParam(value = "serverId") String serverId) {

		int result = consumerServerResourceService.remove(serverId);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/consumerserverinfo", method = RequestMethod.GET)
	@ResponseBody
	public Object loadConsumerSereverInfo() {

		Set<String> hostsSet = new HashSet<String>();
		Set<String> ipsSet = new HashSet<String>();

		Map<String, String> master = ipCollectorService.getProducerServerIpsMap();
		if (master != null) {
			hostsSet.addAll(master.keySet());
			ipsSet.addAll(master.values());
		}

		Map<String, String> slave = ipCollectorService.getConsumerServerMasterIpsMap();
		if (slave != null) {
			hostsSet.addAll(slave.keySet());
			ipsSet.addAll(slave.values());
		}

		return new Pair<List<String>, List<String>>(new ArrayList<String>(hostsSet), new ArrayList<String>(ipsSet));

	}

	@RequestMapping(value = "/console/server/consumer/get/topics", method = RequestMethod.GET)
	@ResponseBody
	public List<String> loadConsumerSereverTopics(@RequestParam String ip) {

		Set<String> result = new HashSet<String>();
		String consumerServerLionConfig = consumerServerResourceService.loadConsumerServerLionConfig();
		if (consumerServerLionConfig == null) {
			return new ArrayList<String>(result);
		}

		Map<String, Set<String>> topicToConsumerServer = parseServerURIString(consumerServerLionConfig);

		for (Map.Entry<String, Set<String>> entry : topicToConsumerServer.entrySet()) {
			Set<String> servers = entry.getValue();
			String topic = entry.getKey();
			if (servers != null && servers.contains(ip) && !result.contains(topic)) {
				result.add(topic);
			}
		}

		if (result.contains(DEFAULT)) {
			result.remove(DEFAULT);
			Set<String> allTopics = topicResourceService.loadCachedTopicToAdministrator().keySet();
			Set<String> allTopicsClone = new HashSet<String>(allTopics);
			Set<String> excludeTopics = topicToConsumerServer.keySet();
			allTopicsClone.removeAll(excludeTopics);
			result.addAll(allTopicsClone);

		}

		return new ArrayList<String>(result);
	}

	@RequestMapping(value = "/console/server/defaultcresource", method = RequestMethod.GET)
	@ResponseBody
	public Object loadDefaultConsumerSereverResource() {

		ConsumerServerResource consumerServerResource = (ConsumerServerResource) consumerServerResourceService
				.findDefault();

		return ConsumerServerResourceMapper.toConsumerServerResourceDto(consumerServerResource);

	}

	@RequestMapping(value = "/console/server/consumer/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editConsumerAlarmSetting(@RequestParam String ip, @RequestParam boolean alarm) {

		ConsumerServerResource consumerServerResource = (ConsumerServerResource) consumerServerResourceService
				.findByIp(ip);
		consumerServerResource.setAlarm(alarm);
		boolean result = consumerServerResourceService.update(consumerServerResource);

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update consumer server alarm of %s to %b successfully", ip, alarm));
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update consumer server alarm of %s to %b fail", ip, alarm));
			}
		}

		return result;
	}

	@RequestMapping(value = "/console/server/mongo")
	public ModelAndView mongoServerSetting(HttpServletRequest request, HttpServletResponse response) {

		subSide = "mongo";
		return new ModelAndView("server/mongo", createViewMap());
	}

	@RequestMapping(value = "/console/server/mongo/list", method = RequestMethod.POST)
	@ResponseBody
	public Object mongoserverSettingList(@RequestBody MongoResourceDto dto) {

		int offset = dto.getOffset();
		int limit = dto.getLimit();
		Pair<Long, List<MongoResource>> pair = mongoResourceService.findMongoResourcePage(offset, limit);
		List<MongoResourceDto> mongoResourceDto = new ArrayList<MongoResourceDto>();
		for (MongoResource mongoResource : pair.getSecond()) {
			mongoResourceDto.add(MongoResourceMapper.toMongoResourceDto(mongoResource));
		}
		return new Pair<Long, List<MongoResourceDto>>(pair.getFirst(), mongoResourceDto);

	}

	@RequestMapping(value = "/console/server/mongo/create", method = RequestMethod.POST)
	@ResponseBody
	public int mongoResourceCreate(@RequestBody MongoResourceDto dto) {

		MongoResource mongoResource = MongoResourceMapper.toMongoResource(dto);
		boolean result = mongoResourceService.update(mongoResource);

		if (!result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/mongo/remove", method = RequestMethod.GET)
	@ResponseBody
	public int remvoeMongoResource(@RequestParam(value = "catalog") String catalog) {

		int result = mongoResourceService.remove(catalog);

		if (result > 0) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}

	@RequestMapping(value = "/console/server/mongoip", method = RequestMethod.GET)
	@ResponseBody
	public Object loadMongoSereverIp() {

		List<String> list = new ArrayList<String>();

		List<MongoResource> mongoResources = mongoResourceService.findAll(DefaultMongoDao.IP);
		for (MongoResource mongoResource : mongoResources) {
			String ip = mongoResource.getIp();
			if (!list.contains(ip)) {
				list.add(ip);
			}
		}

		return list;

	}

	@RequestMapping(value = "/console/server/mongotype", method = RequestMethod.GET)
	@ResponseBody
	public Object loadMongoSereverType() {

		List<MongoType> list = new ArrayList<MongoType>();

		List<MongoResource> mongoResources = mongoResourceService.findAll(DefaultMongoDao.TYPE);
		for (MongoResource mongoResource : mongoResources) {
			MongoType type = mongoResource.getMongoType();
			if (!list.contains(type)) {
				list.add(type);
			}
		}

		return list;

	}

	public static Map<String, Set<String>> parseServerURIString(String value) {

		Map<String, Set<String>> result = new HashMap<String, Set<String>>();

		for (String topicNamesToURI : value.split("\\s*;\\s*")) {

			if (StringUtils.isEmpty(topicNamesToURI)) {
				continue;
			}

			String[] splits = topicNamesToURI.split("=");
			if (splits.length != 2) {
				continue;
			}
			String consumerServerURI = splits[1].trim();
			String[] ipAddrs = consumerServerURI.split(",");
			Set<String> ips = new HashSet<String>();
			if (ipAddrs.length == 2) {
				for (int i = 0; i < 2; ++i) {
					String[] ipPort = ipAddrs[i].split(":");
					if (ipPort.length != 2) {
						continue;
					}
					ips.add(ipPort[0]);
				}
			} else {
				continue;
			}

			String topicNameStr = splits[0].trim();
			result.put(topicNameStr, ips);
		}

		return result;
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
