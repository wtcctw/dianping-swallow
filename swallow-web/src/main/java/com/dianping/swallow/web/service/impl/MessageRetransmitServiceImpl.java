package com.dianping.swallow.web.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MessageRetransmitService;

/**
 * @author mingdongli
 *
 *         2015年5月20日下午6:27:32
 */
@Service("messageRetransmitService")
public class MessageRetransmitServiceImpl extends AbstractSwallowService implements MessageRetransmitService {

	private static final String VERSION = "0.7.1";
	private static final String RETRANSMIT = "retransmit";

	@Autowired
	private MessageDAO<?> messageDAO;

	public boolean retransmitMessage(String topic, long mid) {
		SwallowMessage sm = new SwallowMessage();
		sm = messageDAO.getMessage(topic, mid);
		if (sm != null) { // already exists
			messageDAO.retransmitMessage(topic, sm);
			return true;
		} else {
			logger.info("Error when convert resultset to SwallowMessage");
			return false;
		}
	}

	public void saveNewMessage(String topicName, String content, String type, String delimitor, String property) {
		SwallowMessage sm = new SwallowMessage();
		if (!StringUtils.isEmpty(type)) {
			sm.setType(type);
		}
		if (!StringUtils.isEmpty(property)) {
			Map<String, String> propertyMap = new HashMap<String, String>();
			String[] entrys = property.split(delimitor + delimitor);
			for (String entry : entrys) {
				String[] pair = entry.split(delimitor);
				if (pair.length == 2) {
					propertyMap.put(pair[0], pair[1]);
				}
			}
			sm.setProperties(propertyMap);
		}
		setMessageProperty(sm, content);
		messageDAO.saveMessage(topicName, sm);
	}

	private void setMessageProperty(SwallowMessage sm, String c) {
		sm.setContent(c);
		sm.setVersion(VERSION);
		sm.setGeneratedTime(new Date());

		sm.setSourceIp(IPUtil.getFirstNoLoopbackIP4Address());
		sm.putInternalProperty(RETRANSMIT, "true");
	}

}
