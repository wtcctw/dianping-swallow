package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jodd.util.StringUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.model.Topic;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.TopicService;
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

	public static final String ALL = "all";

	@Resource(name = "topicService")
	private TopicService topicService;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "consumerServerAlarmSettingService")
	private ConsumerServerAlarmSettingService consumerServerAlarmSettingService;

	@Resource(name = "producerServerAlarmSettingService")
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Autowired
	private ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/topic")
	public ModelAndView topicView(HttpServletRequest request, HttpServletResponse response) {

		return new ModelAndView("topic/index", createViewMap());
	}

	@RequestMapping(value = "/console/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object fetchTopicPage(int offset, int limit, String topic, String prop, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		boolean isAllEmpty = StringUtil.isEmpty(topic + prop);

		if (isAllEmpty) {
			String username = extractUsernameUtils.getUsername(request);
			Set<String> adminSet = userService.loadCachedAdministratorSet();
			boolean findAll = adminSet.contains(username) || adminSet.contains(ALL);
			if (findAll) {
				return topicService.loadTopicPage(offset, limit);
			} else {
				return topicService.loadSpecificTopicPage(offset, limit, topic, username);
			}
		} else {
			return topicService.loadSpecificTopicPage(offset, limit, topic, prop);
		}

	}

	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> topicName(HttpServletRequest request, HttpServletResponse response) throws UnknownHostException {

		String username = extractUsernameUtils.getUsername(request);
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean findAll = adminSet.contains(username) || adminSet.contains(ALL);
		return topicService.loadTopicNames(username, findAll);
	}

	@RequestMapping(value = "/console/topic/proposal", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName(HttpServletRequest request, HttpServletResponse response) throws UnknownHostException {

		String username = extractUsernameUtils.getUsername(request);
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean findAll = adminSet.contains(username) || adminSet.contains(ALL);
		return topicService.loadTopicProposal(username, findAll);
	}

	@RequestMapping(value = "/api/topic/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object editTopic(@RequestParam(value = "topic") String topic, @RequestParam(value = "prop") String prop,
			@RequestParam(value = "time") String time,
			@RequestParam(value = "exec_user", required = false) String approver, HttpServletRequest request,
			HttpServletResponse response) {

		String username = extractUsernameUtils.getUsername(request);

		username = StringUtils.isEmpty(username) ? approver : username;

		if (approver != null) {
			if (!userService.loadCachedAdministratorSet().contains(approver)) {
				logger.info(String.format(
						"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed. No authentication!",
						username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
				return ResponseStatus.UNAUTHENTICATION;
			} else {
				Topic t = topicService.loadTopicByName(topic);
				if (t == null) {
					logger.info(String.format(
							"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed. No such topic!",
							username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
					return ResponseStatus.INVALIDTOPIC;
				}
				String proposal = t.getProp();
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

		int result = -1;

		Transaction producerTransaction = Cat.getProducer().newTransaction("TopicEdit", topic + ":" + username);

		try {
			result = topicService.editTopic(topic, prop, time);
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

		if (result == ResponseStatus.SUCCESS.getStatus()) {
			logger.info(String.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] successfully.",
					username, topic, prop, splitProps(prop.trim()).toString(), time.toString()));
			return ResponseStatus.SUCCESS;
		} else if (result == ResponseStatus.MONGOWRITE.getStatus()) {
			logger.info(String.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed.", username,
					topic, prop, splitProps(prop.trim()).toString(), time.toString()));
			return ResponseStatus.MONGOWRITE;
		} else {
			logger.info(String.format(
					"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed.Please try again.", username,
					topic, prop, splitProps(prop.trim()).toString(), time.toString()));
			return ResponseStatus.TRY_MONGOWRITE;
		}

	}

	@RequestMapping(value = "/api/topic/alarm", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void editAlarmSetting(@RequestParam(value = "topic") String topic,
			@RequestParam(value = "alarm") boolean alarm, HttpServletRequest request, HttpServletResponse response) {

		updateConsumerServerAlarmSetting(topic, alarm);
		updateProducerServerAlarmSetting(topic, alarm);

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

	private void updateConsumerServerAlarmSetting(String topic, boolean alarm) {

		ConsumerServerAlarmSetting consumerServerAlarmSetting = consumerServerAlarmSettingService.findDefault();

		if (consumerServerAlarmSetting != null) {
			List<String> topics = consumerServerAlarmSetting.getTopicWhiteList();
			boolean updated = false;

			if (alarm) {
				if (!topics.contains(topic)) {
					topics.add(topic);
					updated = consumerServerAlarmSettingService.update(consumerServerAlarmSetting);
					logger.info(String.format("Add topic %s to whitelist of ConsumerServerAlarmSetting %s", topic,
							updated ? "successfully" : "failed"));
					return;
				}
			} else {
				if (topics.contains(topic)) {
					topics.remove(topic);
					updated = consumerServerAlarmSettingService.update(consumerServerAlarmSetting);
					logger.info(String.format("Remove topic %s from wihtelist of ConsumerServerAlarmSetting %s", topic,
							updated ? "successfully" : "failed"));
					return;
				}
			}

		}
		logger.info(String.format("Nothing need to do about topic %s concerned with ConsumerServerAlarmSetting", topic));
	}

	private void updateProducerServerAlarmSetting(String topic, boolean alarm) {

		ProducerServerAlarmSetting producerServerAlarmSetting = producerServerAlarmSettingService.findDefault();

		if (producerServerAlarmSetting != null) {
			List<String> topics = producerServerAlarmSetting.getTopicWhiteList();
			boolean updated = false;

			if (alarm) {
				if (!topics.contains(topic)) {
					topics.add(topic);
					updated = producerServerAlarmSettingService.update(producerServerAlarmSetting);
					logger.info(String.format("Add topic %s to whitelist of ProducerServerAlarmSetting %s", topic,
							updated ? "successfully" : "failed"));
					return;
				}
			} else {
				if (topics.contains(topic)) {
					topics.remove(topic);
					updated = producerServerAlarmSettingService.update(producerServerAlarmSetting);
					logger.info(String.format("Remove topic %s from wihtelist of ProducerServerAlarmSetting %s", topic,
							updated ? "successfully" : "failed"));
					return;
				}
			}

		}
		logger.info(String.format("Nothing need to do about topic %s concerned with ProducerServerAlarmSetting", topic));
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