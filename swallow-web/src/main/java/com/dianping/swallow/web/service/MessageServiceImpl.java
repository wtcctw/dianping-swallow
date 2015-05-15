package com.dianping.swallow.web.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.WebSwallowMessageDao;
import com.dianping.swallow.web.model.WebSwallowMessage;


/**
 * @author mingdongli
 *
 * 2015年5月14日下午1:20:29
 */
@Service("messageService")
public class MessageServiceImpl extends AbstractSwallowService implements MessageService {

	private static final String 				PRE_MSG 					= "msg#";
	private static final String 				MESSAGE 					= "message";
	private static final String 				GZIP 						= "H4sIAAAAAAAAA";
	
	@Autowired
	private WebSwallowMessageDao 				smdi;
	@Autowired
	private AdministratorDao 					admind;
	
	public Map<String, Object> getMessageFromSpecificTopic(int start,
			int span, String tname, String messageId, String startdt,
			String stopdt, String username) {
		String dbn = PRE_MSG + tname;
		long mid = -1;
		if (!messageId.isEmpty()) { // messageId is not empty
			if (isIP(messageId)) { // query based on IP
				return getByIp(dbn, start, span, messageId, username);
			} else {
				try {
					mid = Long.parseLong(messageId.trim());
				} catch (NumberFormatException e) {
					mid = 0;
				}
			}
		}
		return getResults(dbn, start, span, mid, startdt, stopdt, username);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getByIp(String dbn, int start, int span,
			String ip, String username) {
		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		sizeAndMessage = smdi.findByIp(start, span, ip, subStr);
		beforeResponse(  (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE) );
		return sizeAndMessage;
	}

	// read use readMongoOps
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(String dbn, int start, int span,
			long mid, String startdt, String stopdt, String username) {
		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		if (mid < 0 && (startdt + stopdt).isEmpty()) // just query by topicname
			
			sizeAndMessage = smdi.findByTopicname(start, span, subStr);
		
		else if (startdt == null || startdt.isEmpty()) // time is empty,
			
			sizeAndMessage = smdi.findSpecific(start, span, mid, subStr);
		
		else if (mid < 0) // messageId is empty, query by time
			
			sizeAndMessage = smdi.findByTime(start, span, startdt, stopdt, subStr);
		
		else  // both are not empty, query by time and messageId
			
			sizeAndMessage = smdi.findByTimeAndId(start, span, mid, startdt, stopdt, subStr);

		beforeResponse(  (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE) );
		return sizeAndMessage;
	}
	
	private void beforeResponse(List<WebSwallowMessage> messageList){
		for (WebSwallowMessage m : messageList)
			setSMessageProperty(m, false);
	}
	


	private void setSMessageProperty(WebSwallowMessage m, boolean showcontent) {
		if (!showcontent)
			m.setC("?"); // don't transmit content
		m.setMid(m.get_id());
		if (m.getO_id() != null) {
			m.setMo_id(m.getO_id());
			m.setO_id(null);
		}
		m.setGtstring(m.getGt());
		m.setStstring(m.get_id());
		// no need to transmit
		m.set_id(null);
		isZipped(m);
	}

	private void isZipped(WebSwallowMessage m) {
		if (m.getC().startsWith(GZIP)) {
			try {
				m.setC(ZipUtil.unzip(m.getC()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean isIP(String str) {
		String regex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}

	@SuppressWarnings("unchecked")
	public WebSwallowMessage getMessageContent(String topic, String mid,
			HttpServletRequest request, HttpServletResponse response) {
		
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();

			long messageId = Long.parseLong(mid);
			messageList = (List<WebSwallowMessage>) smdi.findSpecific(0, 1, messageId, topic).get(MESSAGE);
			isZipped(messageList.get(0));
			return messageList.get(0);

	}

}
