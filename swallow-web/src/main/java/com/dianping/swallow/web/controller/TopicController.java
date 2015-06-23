package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.AdministratorService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
public class TopicController extends AbstractMenuController {

	private static final String DELIMITOR = ",";

	public static final String ALL = "all";

	@Resource(name = "topicService")
	private TopicService topicService;

	@Resource(name = "administratorService")
	private AdministratorService administratorService;

	@Autowired
	private ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/topic")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {

		return new ModelAndView("topic/index", createViewMap());
	}

	@RequestMapping(value = "/console/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicDefault(int offset, int limit, String topic,
			String prop, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {
		boolean isAllEmpty = StringUtil.isEmpty(topic + prop);
		if (isAllEmpty) {
			String username = extractUsernameUtils.getUsername(request);
			Set<String> adminSet = administratorService.loadAdminSet();
			boolean findAll = adminSet.contains(username)
					|| adminSet.contains(ALL);
			if (findAll) {
				return topicService.loadAllTopic(offset, limit);
			} else {
				return topicService.loadSpecificTopic(offset, limit, topic,
						username);
			}
		} else {
			return topicService.loadSpecificTopic(offset, limit, topic, prop);
		}

	}

	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> topicName(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		String username = extractUsernameUtils.getUsername(request);
		Set<String> adminSet = administratorService.loadAdminSet();
		boolean findAll = adminSet.contains(username)
				|| adminSet.contains(ALL);
		return topicService.loadAllTopicNames(username, findAll);
	}

	@RequestMapping(value = "/console/topic/propdept", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		String username = extractUsernameUtils.getUsername(request);
		Set<String> adminSet = administratorService.loadAdminSet();
		boolean findAll = adminSet.contains(username)
				|| adminSet.contains(ALL);
		return topicService.getPropAndDept(username, findAll);
	}

	@RequestMapping(value = "/console/topic/auth/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object editTopic(@RequestParam(value = "topic") String topic,
			@RequestParam(value = "prop") String prop,
			@RequestParam(value = "time") String time,
			@RequestParam(value = "exec_user", required = false) String approver,
			HttpServletRequest request, HttpServletResponse response) {

		Map<String, Object> map = new HashMap<String, Object>();
		int result = topicService.editTopic(topic, prop, time);

		if (result == ResponseStatus.SUCCESS.getStatus()) {
			map.put(SaveMessageController.STATUS,
					ResponseStatus.SUCCESS.getStatus());
			map.put(SaveMessageController.MESSAGE,
					ResponseStatus.SUCCESS.getMessage());
			if (StringUtils.isBlank(approver)) {

			}
			logger.info(String
					.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] successfully.",
							extractUsernameUtils.getUsername(request), topic,
							prop, splitProps(prop.trim()).toString(),
							time.toString()));
		} else if (result == ResponseStatus.MONGOWRITE.getStatus()) {
			map.put(SaveMessageController.STATUS,
					ResponseStatus.MONGOWRITE.getStatus());
			map.put(SaveMessageController.MESSAGE,
					ResponseStatus.MONGOWRITE.getMessage());
			logger.info(String
					.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed.",
							extractUsernameUtils.getUsername(request), topic,
							prop, splitProps(prop.trim()).toString(),
							time.toString()));
		} else {
			map.put(SaveMessageController.STATUS,
					ResponseStatus.TRY_MONGOWRITE.getStatus());
			map.put(SaveMessageController.MESSAGE,
					ResponseStatus.TRY_MONGOWRITE.getMessage());
			logger.info(String
					.format("%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ] failed.Please try again.",
							extractUsernameUtils.getUsername(request), topic,
							prop, splitProps(prop.trim()).toString(),
							time.toString()));
		}

		return map;
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