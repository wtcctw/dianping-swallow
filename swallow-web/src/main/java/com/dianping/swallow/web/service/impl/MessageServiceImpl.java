package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.model.Message;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MessageService;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:20:29
 */
@Service("messageService")
public class MessageServiceImpl extends AbstractSwallowService implements
		MessageService {

	private static final String PRE_MSG = "msg#";
	private static final String MESSAGE = "message";
	private static final String GZIP = "H4sIAAAAAAAAA";

	@Autowired
	private MessageDao webSwallowMessageDao;
	@Autowired
	private AdministratorDao administratorDao;

	public Map<String, Object> getMessageFromSpecificTopic(int start, int span,
			String tname, String messageId, String startdt, String stopdt,
			String username, String baseMid) {
		String dbn = PRE_MSG + tname;
		long mid = -1;
		if (!messageId.isEmpty()) { // messageId is not empty
			if (isIP(messageId)) { // query based on IP
				return getByIp(dbn, start, span, messageId, username);
			} else {
				try {
					mid = Long.parseLong(messageId.trim());
				} catch (NumberFormatException e) {
					if (logger.isErrorEnabled()) {
						logger.error("Error when parse " + messageId.trim()
								+ " to Long.", e);
					}
					mid = 0;
				}
			}
		}
		return getResults(dbn, start, span, mid, startdt, stopdt, username,
				baseMid);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getByIp(String dbn, int start, int span,
			String ip, String username) {
		
		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		sizeAndMessage = webSwallowMessageDao.findByIp(start, span, ip, subStr);
		beforeResponse((List<Message>) sizeAndMessage.get(MESSAGE));
		return sizeAndMessage;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(String dbn, int start, int span,
			long mid, String startdt, String stopdt, String username,
			String baseMid) {
		
		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		if (mid < 0 && (startdt + stopdt).isEmpty()) {
			sizeAndMessage = webSwallowMessageDao.findByTopicname(start, span,
					subStr, baseMid);
		} else if (startdt == null || startdt.isEmpty()) {
			sizeAndMessage = webSwallowMessageDao.findSpecific(start, span,
					mid, subStr);
		} else if (mid < 0) {
			sizeAndMessage = webSwallowMessageDao.findByTime(start, span,
					startdt, stopdt, subStr, baseMid);
		} else {
			sizeAndMessage = webSwallowMessageDao.findByTimeAndId(start, span,
					mid, startdt, stopdt, subStr);
		}

		beforeResponse((List<Message>) sizeAndMessage.get(MESSAGE));
		return sizeAndMessage;
	}

	private void beforeResponse(List<Message> messageList) {
		for (Message m : messageList)
			setSMessageProperty(m);
	}

	@Override
	public Map<String, Object> loadMinAndMaxTime(String topicName) {

		Map<String, Object> map = new HashMap<String, Object>();
		map = webSwallowMessageDao.findMinAndMaxTime(topicName);
		return map;
	}

	private void setSMessageProperty(Message m) {
		m.setMid(m.get_id());
		if (m.getO_id() != null) {
			m.setMo_id(m.getO_id());
			m.setO_id(null);
		}
		m.setGtstring(m.getGt());
		m.setStstring(m.get_id());
		m.set_id(null); // no need to transmit
	}

	private void isZipped(Message m) {
		if (m.getC().startsWith(GZIP)) {
			try {
				m.setC(ZipUtil.unzip(m.getC()));
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when unzip " + m.getC(), e);
				}
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
	public Message getMessageContent(String topic, String mid) {

		List<Message> messageList = new ArrayList<Message>();

		long messageId = Long.parseLong(mid);
		messageList = (List<Message>) webSwallowMessageDao.findSpecific(0, 1,
				messageId, topic).get(MESSAGE);
		isZipped(messageList.get(0));
		return messageList.get(0);

	}

}
