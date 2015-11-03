package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午3:31:35
 */
public abstract class AbstractMessageDao extends AbstractDao implements MessageDAO{

	
	@Override
	public void saveMessage(String topicName, SwallowMessage message) {
		saveMessage(topicName, null, message);
	}


}
