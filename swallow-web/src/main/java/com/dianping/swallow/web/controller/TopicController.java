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

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.FilterMetaDataService;
import com.dianping.swallow.web.service.TopicService;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
public class TopicController extends AbstractMenuController {

	private static final String DELIMITOR = ",";

	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;

	@Resource(name = "topicService")
	private TopicService topicService;

	@Autowired
	ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/topic")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("topic/index", map);
	}

	@RequestMapping(value = "/console/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicDefault(int offset, int limit, String topic,
			String prop, String dept, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		boolean findAll = StringUtils.isEmpty(topic + prop + dept);
		if (findAll) {
			return topicService.loadAllTopic(offset, limit);
		} else {
			return topicService.loadSpecificTopic(offset, limit, topic, prop,
					dept);
		}

	}

	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> topicName(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		String userName = extractUsernameUtils.getUsername(request);
		boolean isAdmin = filterMetaDataService.loadAdminSet()
				.contains(userName);
		boolean isproduct = filterMetaDataService.isShowContentToAll();
		return topicService.loadAllTopicNames(userName, isAdmin || isproduct);
	}

	@RequestMapping(value = "/console/topic/propdept", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName() throws UnknownHostException {
		return topicService.getPropAndDept();
	}

	@RequestMapping(value = "/console/topic/auth/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void editTopic(@RequestParam(value = "topic") String topic,
			@RequestParam("prop") String prop,
			@RequestParam("dept") String dept,
			@RequestParam("time") String time, HttpServletRequest request,
			HttpServletResponse response) {

		topicService.editTopic(topic, prop, dept, time);
		logger.info(String.format(
				"%s update topic %s to [prop: %s ], [dept: %s ], [time: %s ].",
				extractUsernameUtils.getUsername(request), topic, prop,
				splitProps(prop.trim()).toString(), time.toString()));

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