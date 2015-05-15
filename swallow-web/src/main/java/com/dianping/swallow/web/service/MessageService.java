package com.dianping.swallow.web.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dianping.swallow.web.model.WebSwallowMessage;


/**
 * @author mingdongli
 *
 * 2015年5月14日下午1:16:39
 */
public interface MessageService extends SwallowService{

	Map<String, Object> getMessageFromSpecificTopic(int start, int span, String tname, 
			String messageId, String startdt, String stopdt, String username);
	
	WebSwallowMessage getMessageContent(String topic, String mid,
			HttpServletRequest request, HttpServletResponse response);
	
}
