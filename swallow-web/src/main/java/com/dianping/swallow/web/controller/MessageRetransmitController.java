package com.dianping.swallow.web.controller;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.web.controller.dto.SendMessageDto;
import com.dianping.swallow.web.controller.dto.SendMessageIDDto;
import com.dianping.swallow.web.service.MessageRetransmitService;
import com.dianping.swallow.web.task.AuthenticationStringGenerator;
import com.dianping.swallow.web.util.ResponseStatus;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.net.UnknownHostException;

@Controller
public class MessageRetransmitController extends AbstractController {

	public static final String STATUS = "status";

	public static final String MESSAGE = "message";

	public static final String DEFAULT_DELIMITOR = ":";

	@Resource(name = "messageRetransmitService")
	private MessageRetransmitService messageRetransmitService;

	@Autowired
	private AuthenticationStringGenerator authenticationStringGenerator;

	@RequestMapping(value = "/api/message/sendmessageid", method = RequestMethod.POST)
	@ResponseBody
	public Object retransmitApi(@RequestBody SendMessageIDDto sendMessageDto) {

		String authentication = sendMessageDto.getAuthentication();
		if (!validateAuthentication(authentication)) {
			return ResponseStatus.UNAUTHENTICATION;
		}
		if (StringUtils.isEmpty(sendMessageDto.getMid())) {
			return ResponseStatus.EMPTYARGU;
		}
		String topicName = sendMessageDto.getTopic();
		String mid = sendMessageDto.getMid();

		return doSendMessageid(topicName, mid);

	}

	@RequestMapping(value = "/console/message/sendmessageid", method = RequestMethod.POST)
	@ResponseBody
	public Object retransmitConsole(@RequestBody SendMessageIDDto sendMessageDto) {

		ResponseStatus status = ResponseStatus.EMPTYARGU;
		if (StringUtils.isBlank(sendMessageDto.getMid())) {
			return status;
		}
		String topicName = sendMessageDto.getTopic();
		String[] mids = sendMessageDto.getMid().split(",");
		for (String mid : mids) {
			status =  doSendMessageid(topicName, mid);
			if(status != ResponseStatus.SUCCESS){
				return status;
			}
		}

		return status;
	}

	private ResponseStatus doSendMessageid(String topic, String mid) {

		boolean successornot = false;
		Transaction producerTransaction = Cat.getProducer().newTransaction("MsgRetransmit",
				topic + ":" + IPUtil.getFirstNoLoopbackIP4Address());
		try {
			successornot = messageRetransmitService.retransmitMessage(topic, Long.parseLong(mid));
			producerTransaction.setStatus(Message.SUCCESS);
		} catch (Exception e) {
			producerTransaction.setStatus(e);
			Cat.getProducer().logError(e);
		} finally {
			producerTransaction.complete();
		}

		if (!successornot) {
			return ResponseStatus.MONGOWRITE;
		}

		return ResponseStatus.SUCCESS;
	}

	@RequestMapping(value = "/api/message/sendmessage", method = RequestMethod.POST)
	@ResponseBody
	public Object sendOneMessagesApi(@RequestBody SendMessageDto sendMessageDto) {

		String authentication = sendMessageDto.getAuthentication();
		if (!validateAuthentication(authentication)) {
			return ResponseStatus.UNAUTHENTICATION;
		}
		return doSendMessage(sendMessageDto.getTopic(), sendMessageDto.getContent(), sendMessageDto.getType(),
				sendMessageDto.getDelimitor(), sendMessageDto.getProperty());
	}

	@RequestMapping(value = "/console/message/sendmessage", method = RequestMethod.POST)
	@ResponseBody
	public Object sendOneMessagesConsole(@RequestBody SendMessageDto sendMessageDto) {

		return doSendMessage(sendMessageDto.getTopic(), sendMessageDto.getContent(), sendMessageDto.getType(),
				sendMessageDto.getDelimitor(), sendMessageDto.getProperty());
	}

	private boolean validateAuthentication(String authentication){
		return authenticationStringGenerator.loadAuthenticationString().equals(authentication);
	}

	private Object doSendMessage(String topic, String text, String type, String delimitor, String property) {
		String topicName = topic.trim();
		String topicType = type.trim();
		String topicProperty = property.trim();
		String textarea = text.trim();
		if (StringUtils.isEmpty(delimitor)) {
			delimitor = DEFAULT_DELIMITOR;
		}
		if (StringUtils.isEmpty(textarea)) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Content is empty"));
			}
			return ResponseStatus.EMPTYCONTENT;
		}

		Transaction producerTransaction = Cat.getProducer().newTransaction("MsgRetransmit",
				topic + ":" + IPUtil.getFirstNoLoopbackIP4Address());

		try {
			messageRetransmitService.saveNewMessage(topicName, textarea, topicType, delimitor, topicProperty);
			producerTransaction.setStatus(Message.SUCCESS);
		} catch (Exception e) {
			producerTransaction.setStatus(e);
			Cat.getProducer().logError(e);
			return ResponseStatus.MONGOWRITE;
		} finally {
			producerTransaction.complete();
		}

		if (logger.isInfoEnabled()) {
			logger.info(String.format("Send all message of %s successfully", topicName));
		}
		return ResponseStatus.SUCCESS;
	}

	@RequestMapping(value = "/console/message/randomstring", method = RequestMethod.GET)
	@ResponseBody
	public String randomString() throws UnknownHostException {

		return authenticationStringGenerator.loadAuthenticationString();
	}

}
