package com.dianping.swallow.producerserver.impl;


import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.lifecycle.Ordered;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.producerserver.MessageReceiver;

/**
 * @author mengwenchao
 * 
 *         2015年4月18日 下午5:18:17
 */
public class AbstractProducerServer extends AbstractLifecycle{

	protected final Logger logger = LogManager.getLogger(getClass());
	
	protected MessageReceiver messageReceiver;

	public static final String producerServerIP = IPUtil
			.getFirstNoLoopbackIP4Address();

	public AbstractProducerServer() {
		
	}
	

	public MessageReceiver getMessageReceiver() {
		return messageReceiver;
	}


	public void setMessageReceiver(MessageReceiver messageReceiver) {
		this.messageReceiver = messageReceiver;
	}


	@Override
	public int getOrder() {
		
		return Ordered.LAST;
	}

}
