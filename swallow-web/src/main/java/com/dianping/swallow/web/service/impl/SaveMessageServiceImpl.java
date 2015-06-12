package com.dianping.swallow.web.service.impl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.SaveMessageService;

/**
 * @author mingdongli
 *
 *         2015年5月20日下午6:27:32
 */
@Service("saveMessageService")
public class SaveMessageServiceImpl extends AbstractSwallowService implements
		SaveMessageService {

	private static final String VERSION = "0.7.1";
	private static final String LOCALHOST = "127.0.0.1";
	private static final String RETRANSMIT = "retransmit";

	@Autowired
	private MessageDAO messageDAO;

	public boolean doRetransmit(String topic, long mid) {
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
		if(!StringUtils.isEmpty(type)){
			sm.setType(type);
		}
		if(!StringUtils.isEmpty(property)){
			Map<String, String> propertyMap = new HashMap<String, String>();
			String[] entrys = property.split(" ");
			for(String entry : entrys){
				String[] pair = entry.split(delimitor);
				if(pair.length == 2){
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
		InetAddress addr = null;

		sm.setSourceIp(getHostIp(addr));
		if (sm.getInternalProperties() != null)
			sm.getInternalProperties().put(RETRANSMIT, "true");
		else {
			Map<String, String> map = new HashMap<String, String>();
			map.put(RETRANSMIT, "true");
			sm.setInternalProperties(map);
		}
	}

	private String getHostIp(InetAddress addr) {
		String ip = null;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();// 获得本机IP
		} catch (UnknownHostException e) {
			ip = LOCALHOST;
			if (logger.isErrorEnabled()) {
				logger.error("Error when getHostAddress.", e);
			}
		}

		return ip;
	}

}
