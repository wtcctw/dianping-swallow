package com.dianping.swallow.web.controller;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.common.internal.util.ZipUtil;
import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.dao.AdministratorDao;
import com.dianping.swallow.web.dao.WebSwallowMessageDaoB;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.WebSwallowMessage;
import com.dianping.swallow.web.service.AccessControlService;
import com.mongodb.MongoClient;

/**
 * @author mingdongli
 *
 *         2015年4月22日 上午12:04:03
 */
@Controller
public class MessageController extends AbstractWriteDao {

	private static final String 				PRE_MSG 					= "msg#";
	private static final String 				SIZE 						= "size";
	private static final String 				SHOW 						= "show";
	private static final String 				MESSAGE 					= "message";
	private static final String 				TOPIC 						= "topic";
	private static final String 				GZIP 						= "H4sIAAAAAAAAA";
	private volatile List<String> 				dbNames 					= new ArrayList<String>();
	private List<MongoClient> 					allReadMongo 				= new ArrayList<MongoClient>();
	private Long 								totalNumOfTopic 			= new Long(0);
	private String 								username;

	@Autowired
	private WebSwallowMessageDaoB 				smdi;
	@Autowired
	private AdministratorDao 					admind;
	@Resource(name = "accessControlService")
	private AccessControlService 				accessControlService;

	@RequestMapping(value = "/console/message")
	public ModelAndView message(HttpServletRequest request,
			HttpServletResponse response) {
		username = setUserName(request);
		Map<String, Object> map = new HashMap<String, Object>();
		return new ModelAndView("message/index", map);
	}
	
	private String setUserName(HttpServletRequest request){
		StringBuffer user = new StringBuffer();
		StringBuffer txz = new StringBuffer();
		WebSwallowUtils.setVisitInfo(request, user, txz);
		return user.toString();
	}

	// doing all query, so use readMongoOps
	@RequestMapping(value = "/console/message/messagedefault", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public Object messageDefault(String offset, String limit, String tname,
			String messageId, String startdt, String stopdt)
			throws UnknownHostException {

		smdi.getTopicNameToMongoMap();
		allReadMongo = smdi.getAllReadMongo();

		for (MongoClient mc : allReadMongo) {
			dbNames.addAll(mc.getDatabaseNames());
		}

		int start = Integer.parseInt(offset);
		int span = Integer.parseInt(limit); // get span+1 topics so that it can
		Map<String, Object> map = new HashMap<String, Object>();
		if (!tname.isEmpty()) {
			map = getMessageFromSpecificTopic(start, span, tname, messageId,
					startdt, stopdt);
		}
		return map;
	}

	private Map<String, Object> getMessageFromSpecificTopic(int start,
			int span, String tname, String messageId, String startdt,
			String stopdt) {
		String dbn = PRE_MSG + tname;
		long mid = -1;
		if (!messageId.isEmpty()) { // messageId is not empty
			if (isIP(messageId)) { // query based on IP
				return getByIp(dbn, start, span, messageId);
			} else {
				try {
					mid = Long.parseLong(messageId.trim());
				} catch (NumberFormatException e) {
					mid = 0;
				}
			}
		}
		return getResults(dbn, start, span, mid, startdt, stopdt);
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getByIp(String dbn, int start, int span,
			String ip) {
		String subStr = dbn.substring(PRE_MSG.length());
		Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
		sizeAndMessage = smdi.findByIp(start, span, ip, subStr);
		totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
		messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);
		for (WebSwallowMessage m : messageList)
			setSMessageProperty(m, false);
		Map<String, Object> map = new HashMap<String, Object>();
		if (accessControlService.getTopicToWhiteList().get(subStr)
				.contains(username))
			map.put(SHOW, true);
		else
			map.put(SHOW, false);
		map.put(SIZE, totalNumOfTopic);
		map.put(TOPIC, messageList);
		return map;
	}

	// read use readMongoOps
	@SuppressWarnings("unchecked")
	private Map<String, Object> getResults(String dbn, int start, int span,
			long mid, String startdt, String stopdt) {
		String subStr = dbn.substring(PRE_MSG.length());
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
		if (mid < 0 && (startdt + stopdt).isEmpty()) { // just query by
														// topicname
			Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
			sizeAndMessage = smdi.findByTopicname(start, span, subStr);
			totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
			messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);
		} else if (startdt == null || startdt.isEmpty()) { // time is empty,
															// query by mid
			messageList = smdi.findSpecific(start, span, mid, subStr);
			if (mid == 0) { // parse error
				totalNumOfTopic = smdi.count(subStr); // set size
			} else
				totalNumOfTopic = (long) messageList.size();
		} else if (mid < 0) { // messageId is empty, query by time
			Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
			sizeAndMessage = smdi.findByTime(start, span, startdt, stopdt,
					subStr);
			totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
			messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);
		} else { // both are not empty, query by time and messageId
			Map<String, Object> sizeAndMessage = new HashMap<String, Object>();
			sizeAndMessage = smdi.findByTimeAndId(start, span, mid, startdt,
					stopdt, subStr);
			totalNumOfTopic = (Long) sizeAndMessage.get(SIZE);
			messageList = (List<WebSwallowMessage>) sizeAndMessage.get(MESSAGE);

		}

		for (WebSwallowMessage m : messageList)
			setSMessageProperty(m, false);
		Map<String, Object> map = new HashMap<String, Object>();
		// update all the time, so get the latest one
		if (accessControlService.getTopicToWhiteList().get(subStr)
				.contains(username)) // read topicToWhiteList
			map.put(SHOW, true);
		else
			map.put(SHOW, false);
		map.put(SIZE, totalNumOfTopic);
		map.put(TOPIC, messageList);
		return map;
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

	// doing all query, so use readMongoOps
	@RequestMapping(value = "/console/message/content", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
	@ResponseBody
	public WebSwallowMessage showMessageContent(String topic, String mid,
			HttpServletRequest request, HttpServletResponse response)
			throws UnknownHostException {
		List<WebSwallowMessage> messageList = new ArrayList<WebSwallowMessage>();
		if (accessControlService.checkVisitIsValid(request, topic)) {
			long messageId = Long.parseLong(mid);
			messageList = smdi.findSpecific(0, 1, messageId, topic);
			isZipped(messageList.get(0));
			return messageList.get(0);
		} else
			return new WebSwallowMessage();
	}

}
