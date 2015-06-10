package com.dianping.swallow.web.controller;

import java.net.UnknownHostException;

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

	@Resource(name = "saveMessageService")
	private SaveMessageService saveMessageService;
	
	@Autowired
	private RandomStringGenerator randomStringGenerator;

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
		int size = mids.length;
		for(int i = size - 1; i >= 0; --i){
			successornot = saveMessageService.doRetransmit(topicName,
					Long.parseLong(mids[i]));
			if (successornot) {
				logger.info(String.format(
						"retransmit messages with mid: %s successfully.",
						mids[i].toString()));
			} else {
				logger.info(String.format(
						"retransmit messages with mid: %s failed.",
						mids[i].toString()));
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
	
	@RequestMapping(value = "/console/message/randomstring", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public String randomString(HttpServletRequest request,
			HttpServletResponse response) throws UnknownHostException {
		
		return randomStringGenerator.loadRandomString();
	}

}
