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

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.web.service.SaveMessageService;
import com.dianping.swallow.web.task.AuthenticationStringGenerator;
import com.dianping.swallow.web.util.ResponseStatus;

@Controller
public class RetransmitMessageController extends AbstractController {

	public static final String STATUS = "status";

	public static final String SEND = "send";

	public static final String MESSAGE = "message";

	public static final String DEFAULT_DELIMITOR = ":";

	@Resource(name = "saveMessageService")
	private SaveMessageService saveMessageService;

	@Autowired
	private AuthenticationStringGenerator authenticationStringGenerator;

	@RequestMapping(value = "/api/message/sendmessageid", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object retransmitapi(@RequestParam(value = "mid") String param, @RequestParam("topic") String topic,
			@RequestParam(value = "authentication", required = false) String authentication,
			HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotBlank(authentication)
				&& !authenticationStringGenerator.loadAuthenticationString().equals(authentication)) {
			return generateResponse(ResponseStatus.UNAUTHENTICATION.getStatus(),
					ResponseStatus.UNAUTHENTICATION.getMessage());
		}
		if (StringUtils.isEmpty(param)) {
			logger.info(String.format("mid is empty"));
			return generateResponse(ResponseStatus.EMPTYCONTENT.getStatus(), ResponseStatus.EMPTYCONTENT.getMessage());
		}
		boolean successornot = false;
		String topicName = topic.trim();
		String[] mids = param.split(",");
		for (String mid : mids) {
			Transaction producerTransaction = Cat.getProducer().newTransaction("MsgRetransmit",
					topic + ":" + IPUtil.getFirstNoLoopbackIP4Address());
			try {
				successornot = saveMessageService.doRetransmit(topicName, Long.parseLong(mid));
				producerTransaction.setStatus(Message.SUCCESS);
			} catch (Exception e) {
				producerTransaction.setStatus(e);
				Cat.getProducer().logError(e);
			} finally {
				producerTransaction.complete();
			}

			if (successornot) {
				logger.info(String.format("retransmit messages with mid: %s successfully.", mid));
			} else {
				logger.info(String.format("retransmit messages with mid: %s failed.", mid));
				return generateResponse(ResponseStatus.MONGOWRITE.getStatus(), ResponseStatus.MONGOWRITE.getMessage());
			}
		}

		return generateResponse(ResponseStatus.SUCCESS.getStatus(), ResponseStatus.SUCCESS.getMessage());
	}

	@RequestMapping(value = "/api/message/sendmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object sendOneMessages(@RequestParam(value = "topic") String topic,
			@RequestParam(value = "content") String text, @RequestParam(value = "type") String type,
			@RequestParam(value = "delimitor", required = false) String delimitor,
			@RequestParam(value = "property") String property,
			@RequestParam(value = "authentication", required = false) String authentication,
			HttpServletRequest request, HttpServletResponse response) {

		if (StringUtils.isNotBlank(authentication)
				&& !authenticationStringGenerator.loadAuthenticationString().equals(authentication)) {
			return generateResponse(ResponseStatus.UNAUTHENTICATION.getStatus(),
					ResponseStatus.UNAUTHENTICATION.getMessage());
		}
		return doSendMessage(topic, text, type, delimitor, property);
	}

	private Object doSendMessage(String topic, String text, String type, String delimitor, String property) {
		String topicName = topic.trim();
		String topicType = type.trim();
		String topicProperty = property.trim();
		String textarea = text.trim();
		if(StringUtils.isEmpty(delimitor)){
			delimitor = DEFAULT_DELIMITOR;
		}
		if (StringUtils.isEmpty(textarea)) {
			logger.info(String.format("Content is empty"));
			return generateResponse(ResponseStatus.EMPTYCONTENT.getStatus(), ResponseStatus.EMPTYCONTENT.getMessage());
		}

		Transaction producerTransaction = Cat.getProducer().newTransaction("MsgRetransmit",
				topic + ":" + IPUtil.getFirstNoLoopbackIP4Address());

		try {
			saveMessageService.saveNewMessage(topicName, textarea, topicType, delimitor, topicProperty);
			producerTransaction.setStatus(Message.SUCCESS);
		} catch (Exception e) {
			producerTransaction.setStatus(e);
			Cat.getProducer().logError(e);
			return generateResponse(ResponseStatus.MONGOWRITE.getStatus(), ResponseStatus.MONGOWRITE.getMessage());
		} finally {
			producerTransaction.complete();
		}

		logger.info(String.format("Send all message of %s successfully", topicName));
		return generateResponse(ResponseStatus.SUCCESS.getStatus(), ResponseStatus.SUCCESS.getMessage());
	}

	private Object generateResponse(int status, String message) {

		Map<String, Object> result = new HashMap<String, Object>();
		result.put(STATUS, status);
		result.put(MESSAGE, message);
		return result;
	}

	@RequestMapping(value = "/console/message/randomstring", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String randomString(HttpServletRequest request, HttpServletResponse response) throws UnknownHostException {

		return authenticationStringGenerator.loadAuthenticationString();
	}

}
