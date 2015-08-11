package com.dianping.swallow.web.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.dao.MessageDao;
import com.dianping.swallow.web.model.Message;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MessageDumpService;
import com.dianping.swallow.web.service.MessageService;

/**
 * @author mingdongli
 *
 *         2015年5月14日下午1:20:29
 */
@Service("messageService")
public class MessageServiceImpl extends AbstractSwallowService implements MessageService {

	private static final String PRE_MSG = "msg#";
	private static final String MESSAGE = "message";
	public static final String GZIP = "H4sIAAAAAAAAA";

	@Autowired
	private MessageDao webMessageDao;

	@Resource(name = "messageDumpService")
	private MessageDumpService messageDumpService;

	public Map<String, Object> getMessageFromSpecificTopic(int start, int span, String tname, String messageId,
			String startdt, String stopdt, String username, String baseMid, boolean sort) {
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
						logger.error("Error when parse " + messageId.trim() + " to Long.", e);
					}
					mid = 0;
				}
			}
		}
		return getResults(dbn, start, span, mid, startdt, stopdt, username, baseMid, sort);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getByIp(String dbn, int start, int span, String ip, String username) {

		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		sizeAndMessage = webMessageDao.findByIp(start, span, ip, subStr);
		beforeResponse((List<Message>) sizeAndMessage.get(MESSAGE));
		return sizeAndMessage;
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(String dbn, int start, int span, long mid, String startdt, String stopdt,
			String username, String baseMid, boolean sort) {

		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		if (mid < 0 && (startdt + stopdt).isEmpty()) {
			sizeAndMessage = webMessageDao.findByTopicname(start, span, subStr, baseMid, sort);
		} else if (StringUtils.isEmpty(startdt)) {
			sizeAndMessage = webMessageDao.findSpecific(start, span, mid, subStr, sort);
		} else if (mid < 0) {
			sizeAndMessage = webMessageDao.findByTime(start, span, startdt, stopdt, subStr, baseMid, sort);
		} else {
			sizeAndMessage = webMessageDao.findByTimeAndId(start, span, mid, startdt, stopdt, subStr);
		}

		beforeResponse((List<Message>) sizeAndMessage.get(MESSAGE));
		return sizeAndMessage;
	}

	private void beforeResponse(List<Message> messageList) {
		for (Message m : messageList)
			setSMessageProperty(m);
	}

	@Override
	public long loadTimeOfFirstMessage(String topicName) {

		Message msg = webMessageDao.loadFirstMessage(topicName);
		
		if(msg != null){
			int seconds = msg.get_id().getTime();
			return new Long(seconds) * 1000;
		}else{
			return -1;
		}
		
	}

	private void setSMessageProperty(Message m) {
		m.setMid(m.get_id());
		if (m.getO_id() != null) {
			m.setMo_id(m.getO_id());
			m.setO_id(null);
		}
		m.setGtstring(m.getGt());
		m.setStstring(m.get_id());
		m.set_id(null);

		String internalP = m.get_p();
		
		if(internalP == null){
			m.setRetransmit("");
		}else{
			try {
				JSONObject json = new JSONObject(internalP);
				String retrans = json.getString("retransmit");
				if (retrans != null && !retrans.equals("true")) {
					m.setRetransmit(retrans);
				} else {
					m.setRetransmit("");
				}
			} catch (JSONException e) {
				logger.info("no save_time in o_ip");
			}
		}
	}

	public void isZipped(Message m) {
		String content = m.getC();
		if (StringUtils.isNotEmpty(content) && m.getC().startsWith(GZIP)) {
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
		messageList = (List<Message>) webMessageDao.findSpecific(0, 1, messageId, topic, false).get(MESSAGE);
		Message m = messageList.get(0);
		isZipped(m);
		prettyDisplay(m);
		return m;

	}

	private void prettyDisplay(Message m) {
		m.setGtstring(m.getGt());
		String internalP = m.get_p();
		JSONObject json;
		try {
			json = new JSONObject(internalP);
			Long time = json.getLong("save_time");
			String ststring = new SimpleDateFormat(Message.TIMEFORMAT).format(time);
			json.put("save_time", ststring);
			m.set_p(json.toString().replaceAll("\"", ""));
		} catch (JSONException e) {
			logger.info("no save_time in o_ip");
		}
	}

	@Override
	public Map<String, Object> exportMessage(String topicName, String startdt, String stopdt) {

		return webMessageDao.exportMessages(topicName, startdt, stopdt);
	}

}
