package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

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

import com.dianping.swallow.web.service.SaveMessageService;
import com.dianping.swallow.web.task.RandomStringGenerator;

@Controller
public class SaveMessageController extends AbstractController {

	private static final String STATUS = "status";
	
	private static final String RETRANSMIT = "retransmit";
	
	@Resource(name = "saveMessageService")
	private SaveMessageService saveMessageService;

	@Autowired
	private RandomStringGenerator randomStringGenerator;

	@RequestMapping(value = "/console/message/auth/sendmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object retransmit(@RequestParam(value = "mids") String param,
			@RequestParam("topic") String topic) {

		int send = 0;
		boolean successornot = false;
		if (StringUtils.isEmpty(param)) {
			return failResponse(send);
		}

		String topicName = topic.trim();
		String[] mids = param.split(",");
		for (String mid : mids) {
			successornot = saveMessageService.doRetransmit(topicName,
					Long.parseLong(mid));
			if (successornot) {
				logger.info(String.format(
						"retransmit messages with mid: %s successfully.", mid));
				++send;
			} else {
				logger.info(String.format(
						"retransmit messages with mid: %s failed.", mid));
				return failResponse(send);
			}
		}
		return successResponse(send);
	}

	@RequestMapping(value = "/console/message/auth/sendgroupmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object sendGroupMessages(@RequestParam(value = "topic") String topic,
			@RequestParam("textarea") String text,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "property") String property,
			HttpServletRequest request, HttpServletResponse response) {

		int send = 0;
		String topicName = topic.trim();
		String topicType = type.trim();
		String topicProperty = property.trim();
		String textarea = text.trim();
		if (StringUtils.isEmpty(textarea)) {
			logger.info(String.format("Content is empty"));
			return failResponse(send);
		}
		String[] contents = textarea.split("\\n");
		int pieces = contents.length;

		for (int i = 0; i < pieces; ++i) {
			try{
				saveMessageService.saveNewMessage(topicName, contents[i], topicType ,topicProperty);
				++send;
			}catch(Exception e){
				return failResponse(send);
			}
		}
		return successResponse(send);
	}
	
	private Object failResponse(int send){
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(STATUS, "fail");
		result.put(RETRANSMIT, send);
		return result;
	}
	
	private Object successResponse(int send){
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(STATUS, "success");
		result.put(RETRANSMIT, send);
		return result;
	}

	@RequestMapping(value = "/console/message/randomstring", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String randomString(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		return randomStringGenerator.loadRandomString();
	}

}
