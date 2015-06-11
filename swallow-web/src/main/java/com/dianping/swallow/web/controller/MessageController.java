package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.model.Message;
import com.dianping.swallow.web.service.MessageService;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractMenuController {

	@Resource(name = "messageService")
	private MessageService messageService;

	@Autowired
	ExtractUsernameUtils extractUsernameUtils;

	@RequestMapping(value = "/console/message")
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response) {

		return new ModelAndView("message/index", createViewMap());
	}

	@RequestMapping(value = "/console/message/messagedefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object messageDefault(int offset, int limit, String tname,
			String messageId, String startdt, String stopdt,
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> map = new HashMap<String, Object>();
		String username = extractUsernameUtils.getUsername(request);

		map = messageService.getMessageFromSpecificTopic(offset, limit, tname,
				messageId, startdt, stopdt, username);
		return map;
	}

	@RequestMapping(value = "/console/message/timespan", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object getMinAndMaxTime(String topic, HttpServletRequest request,
			HttpServletResponse response) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map = messageService.loadMinAndMaxTime(topic);
		return map;
	}

	@RequestMapping(value = "/console/message/auth/content", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Message showMessageContent(String topic, String mid,
			HttpServletRequest request, HttpServletResponse response)
			throws UnknownHostException {

		return messageService.getMessageContent(topic, mid);
	}

	@Override
	protected String getMenu() {

		return "message";
	}

}
