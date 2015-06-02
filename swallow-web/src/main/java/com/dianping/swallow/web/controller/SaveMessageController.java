package com.dianping.swallow.web.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.web.service.SaveMessageService;

@Controller
public class SaveMessageController extends AbstractController {

	@Resource(name = "saveMessageService")
	private SaveMessageService saveMessageService;

	@RequestMapping(value = "/console/message/auth/sendmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void retransmit(@RequestParam(value = "param") String param,
			@RequestParam("topic") String topic) {
		boolean successornot = false;
		if (StringUtils.isEmpty(param)) {
			return;
		}

		String topicName = topic.trim();
		String[] mids = param.split(",");
		for (String mid : mids) {
			successornot = saveMessageService.doRetransmit(topicName,
					Long.parseLong(mid));
			if (successornot) {
				logger.info(String.format(
						"retransmit messages with mid: %s successfully.",
						mid.toString()));
			} else {
				logger.info(String.format(
						"retransmit messages with mid: %s failed.",
						mid.toString()));
			}
		}

	}

	@RequestMapping(value = "/console/message/auth/sendgroupmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
	@ResponseBody
	public void sendGroupMessages(@RequestParam(value = "topic") String topic,
			@RequestParam("textarea") String text, HttpServletRequest request,
			HttpServletResponse response) {
		String topicName = topic.trim();
		String textarea = text.trim();
		if (StringUtils.isEmpty(textarea)) {
			logger.info(String.format("Content is empty"));
			return;
		}
		String[] contents = textarea.split("\\n");
		int pieces = contents.length;

		for (int i = 0; i < pieces; ++i) {
			saveMessageService.saveNewMessage(topicName, contents[i]);
		}
	}

}
