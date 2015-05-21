package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.model.WebSwallowMessage;
import com.dianping.swallow.web.service.MessageService;
import com.dianping.swallow.web.service.TopicService;
import com.dianping.swallow.web.service.impl.AccessControlServiceImpl;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractController{

	private static final String 				SHOW 						= "show";
	
	@Resource(name = "accessControlService")
	private AccessControlServiceImpl 				accessControlService;
	
    @Resource(name = "topicService")
    private TopicService 							topicService;
    
	@Resource(name = "messageService")
	private MessageService 							messageService;

	@RequestMapping(value = "/console/message")
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("message/index", map);
	}
	
	private String setUserName(HttpServletRequest request){
		
		return WebSwallowUtils.getVisitInfo(request);
	}

	@RequestMapping(value = "/console/message/messagedefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object messageDefault(String offset, String limit, String tname, String messageId, 
			String startdt, String stopdt, HttpServletRequest request, HttpServletResponse response){
		topicService.saveVisitInAdminList(WebSwallowUtils.getVisitInfo(request));
		Map<String, Object> map = new HashMap<String,Object>();
		String username = setUserName(request);
		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit); // get span+1 topics so that it can
			
		map = messageService.getMessageFromSpecificTopic(start, span, tname, messageId, startdt, stopdt, username);
		return getResponse(map, tname, username);
	}

	@RequestMapping(value = "/console/message/content", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public WebSwallowMessage showMessageContent(String topic, String mid,
			HttpServletRequest request, HttpServletResponse response)
			throws UnknownHostException {
		if (accessControlService.checkVisitIsValid(request, topic)) {
			return messageService.getMessageContent(topic, mid);
		} else{
			return new WebSwallowMessage();
		}
	}
	
	private Map<String, Object> getResponse(Map<String, Object> map, String topicName, String username){
		if (accessControlService.getTopicToWhiteList().get(topicName)
				.contains(username)) // read topicToWhiteList
			map.put(SHOW, true);
		else
			map.put(SHOW, false);
		return map;
	}

}
