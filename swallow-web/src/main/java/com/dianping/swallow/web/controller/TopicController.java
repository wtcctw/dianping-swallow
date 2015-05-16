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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.service.TopicService;

/**
 * @author mingdongli
 *
 *         2015年4月22日 下午1:50:20
 */
@Controller
public class TopicController extends AbstractWriteDao {
	
	private static final String DELIMITOR = ",";

	@Resource(name = "topicService")
	private TopicService topicService;

	@RequestMapping(value = "/console/topic")
	public ModelAndView allApps(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("topic/index", map);
	}

	@RequestMapping(value = "/console/topic/topicdefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object topicDefault(String offset, String limit, String name,
			String prop, String dept, HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {
		//save visit info
		administratorService.saveVisitAdmin(WebSwallowUtils.getVisitInfo(request));
		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit); // get span+1 topics so that it can
		boolean findAll = (name + prop + dept).isEmpty();
		WebSwallowUtils.getVisitInfo(request);
		if (findAll)
			return topicService.getAllTopicFromExisting(start, span);
		else
			return topicService.getSpecificTopic(start, span, name, prop, dept);

	}
	

	// read from readMongoOps
	@RequestMapping(value = "/console/topic/namelist", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public List<String> topicName() throws UnknownHostException {

		return topicService.getTopicNames();
	}

	// read from writeMongoOps, everytime read the the database to get the
	// latest info
	@RequestMapping(value = "/console/topic/propdept", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object propName() throws UnknownHostException {

		return topicService.getPropAndDept().toArray();

	}

	@RequestMapping(value = "/console/topic/edittopic", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void editTopic(@RequestParam(value = "name") String name,
			@RequestParam("prop") String prop,
			@RequestParam("dept") String dept,
			@RequestParam("time") String time, HttpServletRequest request,
			HttpServletResponse response) {

		if (accessControlService.checkVisitIsValid(request)) {
			
			accessControlService.getTopicToWhiteList().put(name, splitProps(prop.trim()));
			topicService.editTopic(name, prop, dept, time);
			
		}
		return;
	}
	
	private Set<String> splitProps(String props) {
		String[] prop = props.split(DELIMITOR);
		Set<String> lists = new HashSet<String>(Arrays.asList(prop));
		return lists;
	}

}