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
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class SaveMessageController extends AbstractController {

	public static final String STATUS = "status";
	
	public static final String SEND = "send";
	
	public static final String MESSAGE = "message";

	public static final String DEFAULT_DELIMITOR = ":";

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
			logger.info(String.format("mid is empty"));
			return generateResponse(send, ResponseStatus.E_EMPTYCONTENT, ResponseStatus.M_EMPTYCONTENT);
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
				return generateResponse(send, ResponseStatus.E_MONGOWRITE, ResponseStatus.M_MONGOWRITE);
			}
		}
		
		logger.info(String.format("Send all message of %s successfully", topicName));
		return generateResponse(send, ResponseStatus.SUCCESS, ResponseStatus.M_SUCCESS);
	}

	@RequestMapping(value = "/console/message/auth/sendonemessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object sendOneMessages(@RequestParam(value = "topic") String topic,
			@RequestParam(value = "textarea") String text,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "delimitor") String delimitor,
			@RequestParam(value = "property") String property,
			HttpServletRequest request, HttpServletResponse response) {

		int send = 0;
		String topicName = topic.trim();
		String topicType = type.trim();
		String topicProperty = property.trim();
		String textarea = text.trim();
		if (StringUtils.isEmpty(textarea)) {
			logger.info(String.format("Content is empty"));
			return generateResponse(send, ResponseStatus.E_EMPTYCONTENT, ResponseStatus.M_EMPTYCONTENT);
		}
		
		try {
			saveMessageService.saveNewMessage(topicName, textarea,
					topicType, delimitor, topicProperty);
			++send;
		} catch (Exception e) {
			return generateResponse(send, ResponseStatus.E_MONGOWRITE, ResponseStatus.M_MONGOWRITE);
		}
		
		logger.info(String.format("Send all message of %s successfully", topicName));
		return generateResponse(send, ResponseStatus.SUCCESS, ResponseStatus.M_SUCCESS);
	}
	
	@RequestMapping(value = "/console/message/auth/sendgroupmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object sendGroupMessages(@RequestParam(value = "topic") String topic,
			@RequestParam(value = "textarea[]") String[] textarea,
			@RequestParam(value = "type") String type,
			@RequestParam(value = "property") String property,
			@RequestParam(value = "delimitor", required = false) String delimitor,
			HttpServletRequest request, HttpServletResponse response) {

		int send = 0;
		String delim = StringUtils.isEmpty(delimitor) ? DEFAULT_DELIMITOR :delimitor;
		String topicName = topic.trim();
		String topicType = type.trim();
		String topicProperty = property.trim();
		int pieces = textarea.length;
		if (pieces == 0) {
			logger.info(String.format("Content is empty"));
			return generateResponse(send, ResponseStatus.E_EMPTYCONTENT, ResponseStatus.M_EMPTYCONTENT);
		}

		for (int i = 0; i < pieces; ++i) {
			if(StringUtils.isEmpty(textarea[i])){
				continue;
			}
			try{
				saveMessageService.saveNewMessage(topicName, textarea[i], topicType ,delim, topicProperty);
				++send;
			}catch(Exception e){
				return generateResponse(send, ResponseStatus.E_MONGOWRITE, ResponseStatus.M_MONGOWRITE);
			}
		}
		logger.info(String.format("Send message of topic %s successfully", topicName));
		return generateResponse(send, ResponseStatus.SUCCESS, ResponseStatus.M_SUCCESS);
	}
	
	private Object generateResponse(int send, int status, String message){
		
		Map<String, Object> result = new HashMap<String, Object>();
		result.put(STATUS, status);
		result.put(SEND, send);
		result.put(MESSAGE, message);
		return result;
	}
	
	@RequestMapping(value = "/console/message/randomstring", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String randomString(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {

		return randomStringGenerator.loadRandomString();
	}

}
