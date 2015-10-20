package com.dianping.swallow.web.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.controller.dto.TopicResourceDto;
import com.dianping.swallow.web.controller.mapper.TopicResourceMapper;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.model.Administrator;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.util.ResponseStatus;
import com.mongodb.MongoException;
import com.mongodb.MongoSocketException;
import jodd.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

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
	public ModelAndView topicView() {

		return new ModelAndView("topic/index", createViewMap());
	}

	@RequestMapping(value = "/console/topic/list", method = RequestMethod.POST)
	@ResponseBody
	public Object fetchTopicPage(@RequestBody TopicQueryDto topicQueryDto, HttpServletRequest request) {

		List<TopicResourceDto> result = new ArrayList<TopicResourceDto>();
		Pair<Long, List<TopicResource>> pair = new Pair<Long, List<TopicResource>>();
		String topic = topicQueryDto.getTopic();
		String producerIp = topicQueryDto.getProducerServer();
		boolean inactive = topicQueryDto.isInactive();
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();

		boolean isAllEmpry = StringUtil.isAllBlank(topic, producerIp) && inactive;

		if (isAllEmpry) {
			String username = userUtils.getUsername(request);
			boolean findAll = userUtils.isAdministrator(username);
			if (findAll) {
				pair = topicResourceService.findTopicResourcePage(offset, limit);
			} else {
				pair = topicResourceService.findByAdministrator(offset, limit, username);
			}
		} else {
			pair = topicResourceService.find(offset, limit, topic, producerIp, inactive);
		}

		for (TopicResource topicResource : pair.getSecond()) {
			result.add(TopicResourceMapper.toTopicResourceDto(topicResource));
		}
		return new Pair<Long, List<TopicResourceDto>>(pair.getFirst(), result);
	}

	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET)
	@ResponseBody
	public Pair<List<String>, List<String>> topicAndIp(HttpServletRequest request) {

		String username = userUtils.getUsername(request);
		List<String> topics = userUtils.topicNames(username);
		List<String> ips = userUtils.producerIps(username);

		return new Pair<List<String>, List<String>>(topics, ips);
	}

	@RequestMapping(value = "/console/topic/update", method = RequestMethod.POST)
	@ResponseBody
	public boolean updateTopic(@RequestBody TopicResourceDto topicResourceDto) {

		TopicResource topicResource = TopicResourceMapper.toTopicResource(topicResourceDto);
		return topicResourceService.update(topicResource);
	}

	@RequestMapping(value = "/console/topic/auth/ip", method = RequestMethod.POST)
	@ResponseBody
	public Object queryProducerIp(@RequestBody TopicQueryDto topicQueryDto) {

		TopicResourceDto topicResourceDto = null;
		String topic = topicQueryDto.getTopic();
		TopicResource topicResource = topicResourceService.findByTopic(topic);
		if (topicResource != null) {
			topicResourceDto = TopicResourceMapper.toTopicResourceDto(topicResource);
		}

		return topicResourceDto;
	}

	@RequestMapping(value = "/api/topic/edittopic", method = RequestMethod.POST)
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
				prop = checkProposalName(prop);
				if (StringUtils.isNotEmpty(proposal)) {
					StringBuffer sb = new StringBuffer();
					sb.append(proposal).append(DELIMITOR).append(prop);
					String[] propsals = sb.toString().split(DELIMITOR);
					Set<String> propsalSet = new HashSet<String>(Arrays.asList(propsals));
					prop = StringUtils.join(propsalSet, DELIMITOR);
				}
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
	public boolean editProducerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);
		topicResource.setProducerAlarm(alarm);
		return topicResourceService.update(topicResource);
	}

	@RequestMapping(value = "/console/topic/consumer/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean editConsumerAlarmSetting(@RequestParam String topic, @RequestParam boolean alarm) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);
		topicResource.setConsumerAlarm(alarm);
		return topicResourceService.update(topicResource);
	}

	@RequestMapping(value = "/console/topic/administrator", method = RequestMethod.GET)
	@ResponseBody
	public Object loadAdministrators() {

		return  userUtils.administrator();
	}
	
	@RequestMapping(value = "/console/topic/alarm/ipinfo/alarm", method = RequestMethod.GET)
	@ResponseBody
	public boolean setAlarm(String topic, String ip, boolean alarm) {

		return doSetIpInfo(topic, ip, "alarm", alarm);
	}

	@RequestMapping(value = "/console/topic/alarm/ipinfo/active", method = RequestMethod.GET)
	@ResponseBody
	public boolean setActive(String topic, String cid, String ip, boolean active) {

		return doSetIpInfo(topic, ip, "active", active);
	}

	private boolean doSetIpInfo(String topic, String ip, String type, boolean value) {

		TopicResource topicResource = topicResourceService.findByTopic(topic);
		List<IpInfo> ipInfos = topicResource.getProducerIpInfos();
		if (ipInfos == null || ip == null || type == null) {
			return false;
		}
		for (IpInfo ipInfo : ipInfos) {
			if (ip.equals(ipInfo.getIp())) {
				if (type.equals("alarm")) {
					ipInfo.setAlarm(value);
				} else if (type.equals("active")) {
					ipInfo.setActive(value);
				} else {
					return false;
				}
				topicResource.setProducerIpInfos(ipInfos);
				return topicResourceService.insert(topicResource);
			}
		}

		return false;
	}
	
	@RequestMapping(value = "/console/topic/alarm/ipinfo/count/inactive", method = RequestMethod.GET)
	@ResponseBody
	public long countInactive() {

		return topicResourceService.countInactive();
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

	@Override
	protected String getMenu() {
		return "topic";
	}

}