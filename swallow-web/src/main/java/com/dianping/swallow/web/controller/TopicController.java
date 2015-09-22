package com.dianping.swallow.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.controller.dto.TopicResourceDto;
import com.dianping.swallow.web.controller.mapper.TopicResourceMapper;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
public class TopicController extends AbstractMenuController {

	private static final String DELIMITOR = ",";

	private static final String POSTFIX = "@dianping.com";

	public static final String DEFAULT = "default";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Autowired
	private UserUtils userUtils;

	@RequestMapping(value = "/console/topic")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("topic/index", createViewMap());
	}

	@RequestMapping(value = "/console/topic/list", method = RequestMethod.POST)
	@ResponseBody
	public Object fetchTopicPage(@RequestBody TopicQueryDto topicQueryDto, HttpServletRequest request,
			HttpServletResponse response) {

		List<TopicResourceDto> result = new ArrayList<TopicResourceDto>();
		Pair<Long, List<TopicResource>> pair = new Pair<Long, List<TopicResource>>();
		String topic = topicQueryDto.getTopic();
		String producerIp = topicQueryDto.getProducerServer();
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();

		boolean isAllEmpry = StringUtil.isAllBlank(topic, producerIp);

		if (isAllEmpry) {
			String username = userUtils.getUsername(request);
			boolean findAll = userUtils.isAdministrator(username);
			if (findAll) {
				pair = topicResourceService.findTopicResourcePage(offset, limit);
			} else {
				pair = topicResourceService.findByAdministrator(offset, limit, username);
			}
		} else {
			pair = topicResourceService.find(offset, limit, topic, producerIp);
		}

		for (TopicResource topicResource : pair.getSecond()) {
			result.add(TopicResourceMapper.toTopicResourceDto(topicResource));
		}
		return new Pair<Long, List<TopicResourceDto>>(pair.getFirst(), result);
	}

	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET)
	@ResponseBody
	public Pair<List<String>, List<String>> topicName(HttpServletRequest request) {

		String username = userUtils.getUsername(request);
		boolean findAll = userUtils.isAdministrator(username);
		Set<String> producerIp = new HashSet<String>();

		Map<String, Set<String>> topicToWhiteList = topicResourceService.loadCachedTopicToAdministrator();
		if (findAll) {
			List<String> topics = new ArrayList<String>(topicToWhiteList.keySet());
			List<TopicResource> topicResources = topicResourceService.findAll();
			List<String> tmpips;
			for (TopicResource topicResource : topicResources) {
				tmpips = topicResource.getProducerIps();
				if (tmpips != null) {
					producerIp.addAll(tmpips);
				}
			}
			if (userUtils.isTrueAdministrator(username)) {
				topics.add(DEFAULT);
			}
			return new Pair<List<String>, List<String>>(topics, new ArrayList<String>(producerIp));
		} else {
			List<String> topics = new ArrayList<String>();
			for (Map.Entry<String, Set<String>> entry : topicToWhiteList.entrySet()) {
				if (entry.getValue().contains(username)) {
					String topic = entry.getKey();
					if (!topics.contains(topic)) {
						topics.add(topic);
					}
				}
			}

			for (String topic : topics) {
				List<String> tmpips = topicResourceService.findByTopic(topic).getProducerIps();
				if (tmpips != null) {
					producerIp.addAll(tmpips);
				}
			}
			return new Pair<List<String>, List<String>>(topics, new ArrayList<String>(producerIp));
		}
	}

	@RequestMapping(value = "/console/topic/proposal", method = RequestMethod.GET)
	@ResponseBody
	public Object propName(HttpServletRequest request, HttpServletResponse response) {

		String username = userUtils.getUsername(request);
		boolean findAll = userUtils.isAdministrator(username);

		Set<String> editProposal = new HashSet<String>();
		List<TopicResource> topicResources = topicResourceService.findAll();

		if (findAll) {
			for (TopicResource topicResource : topicResources) {
				editProposal.addAll(getPropList(topicResource));
			}
		} else {
			for (TopicResource topicResource : topicResources) {
				Set<String> tmpprop = getPropList(topicResource);
				if (tmpprop.contains(username)) {
					editProposal.addAll(tmpprop);
				}
			}

		}

		List<Administrator> adminList = userService.loadUsers();
		for (Administrator admin : adminList) {
			editProposal.add(admin.getName());
		}

		return new ArrayList<String>(editProposal);
	}

	@RequestMapping(value = "/console/topic/update", method = RequestMethod.POST)
	@ResponseBody
	public Object updateTopic(@RequestBody TopicResourceDto topicResourceDto) {

		TopicResource topicResource = TopicResourceMapper.toTopicResource(topicResourceDto);
		boolean result = topicResourceService.update(topicResource);

		if (result) {
			return ResponseStatus.SUCCESS.getStatus();
		} else {
			return ResponseStatus.MONGOWRITE.getStatus();
		}
	}
	
	@RequestMapping(value = "/console/topic/auth/ip", method = RequestMethod.POST)
	@ResponseBody
	public Object queryProducerIp(@RequestBody TopicQueryDto topicQueryDto) {

		TopicResourceDto topicResourceDto = null;
		String topic = topicQueryDto.getTopic();
		TopicResource topicResource = topicResourceService.findByTopic(topic);
		if(topicResource != null){
			topicResourceDto = TopicResourceMapper.toTopicResourceDto(topicResource);
		}
		
		return topicResourceDto;
	}

	@RequestMapping(value = "/api/topic/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object editTopic(@RequestParam(value = "topic") String topic, @RequestParam(value = "prop") String prop,
			@RequestParam(value = "time") String time, @RequestParam(value = "exec_user") String approver,
			HttpServletRequest request, HttpServletResponse response) {

		String username = userUtils.getUsername(request);
		TopicResource topicResource = null;

		username = StringUtils.isEmpty(username) ? approver : username;

		if (approver != null) {
			if (!userService.loadCachedAdministratorSet().contains(approver)) {
				if (logger.isInfoEnabled()) {
					logger.info(String.format(
							"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed. No authentication!",
							username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
				}
				return ResponseStatus.UNAUTHENTICATION;
			} else {
				topicResource = topicResourceService.findByTopic(topic);
				if (topicResource == null) {
					if (logger.isInfoEnabled()) {
						logger.info(String.format(
								"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed. No such topic!",
								username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
					}
					return ResponseStatus.INVALIDTOPIC;
				}
				String proposal = topicResource.getAdministrator();
				StringBuffer sb = new StringBuffer();
				prop = checkProposalName(prop);
				if (StringUtils.isNotEmpty(proposal)) {
					sb.append(proposal).append(",").append(prop);
				} else {
					sb.append(prop);
				}
				prop = sb.toString();
			}
		}

		boolean result = false;

		Transaction producerTransaction = Cat.getProducer().newTransaction("TopicEdit", topic + ":" + username);

		try {
			topicResource.setAdministrator(prop);
			topicResource.setUpdateTime(new Date());
			result = topicResourceService.update(topicResource);
			producerTransaction.setStatus(Message.SUCCESS);
		} catch (MongoSocketException e) {
			producerTransaction.setStatus(e);
			Cat.getProducer().logError(e);
		} catch (MongoException e) {
			producerTransaction.setStatus(e);
			Cat.getProducer().logError(e);
		} finally {
			producerTransaction.complete();
		}

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] successfully.",
						username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
			}
			return ResponseStatus.SUCCESS;
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format(
						"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed.Please try again.",
						username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
			}
			return ResponseStatus.MONGOWRITE;
		}

	}

	@RequestMapping(value = "/console/topic/producer/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editProducerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm,
			HttpServletRequest request, HttpServletResponse response) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);
		topicResource.setProducerAlarm(alarm);
		boolean result = topicResourceService.update(topicResource);

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update producer alarm of %s to %b successfully", topic, alarm));
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update producer alarm of %s to %b fail", topic, alarm));
			}
		}

		return result;
	}

	@RequestMapping(value = "/console/topic/consumer/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editConsumerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm,
			HttpServletRequest request, HttpServletResponse response) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);
		topicResource.setConsumerAlarm(alarm);
		boolean result = topicResourceService.update(topicResource);

		if (result) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update consumer alarm of %s to %b successfully", topic, alarm));
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Update consumer alarm of %s to %b fail", topic, alarm));
			}
		}

		return result;
	}

	@RequestMapping(value = "/console/topic/administrator", method = RequestMethod.GET)
	@ResponseBody
	public Object loadAdministrators() {

		Set<String> administrators = new HashSet<String>();

		List<Administrator> adminList = userService.loadUsers();

		for (Administrator administrator : adminList) {
			administrators.add(administrator.getName());
		}

		List<TopicResource> topicResources = topicResourceService.findAll();
		for (TopicResource topicResource : topicResources) {
			String whiteListString = topicResource.getAdministrator();
			String[] whiteList = whiteListString.split(DELIMITOR);
			for (String wl : whiteList) {
				administrators.add(wl);
			}
		}

		return administrators;
	}

	private String checkProposalName(String proposal) {

		if (proposal.contains(POSTFIX)) {
			int index = proposal.indexOf(POSTFIX);
			proposal = proposal.substring(0, index);
		}
		int index = proposal.indexOf("?");
		if (index != -1) {
			proposal = proposal + "，";
			proposal = proposal.replaceAll("\\?", ",").replaceAll(" ", "").replaceAll("，", ",");
		}
		return proposal;
	}

	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));

		return lists;
	}

	private Set<String> getPropList(TopicResource topicResource) {
		Set<String> props = new HashSet<String>();
		String[] tmpprops = topicResource.getAdministrator().split(",");

		for (String tmpProp : tmpprops) {
			if (!StringUtils.isEmpty(tmpProp)) {
				props.add(tmpProp);
			}
		}
		return props;
	}

	@Override
	protected String getMenu() {
		return "topic";
	}

}