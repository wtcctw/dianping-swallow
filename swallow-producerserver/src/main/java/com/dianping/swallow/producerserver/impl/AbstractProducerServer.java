package com.dianping.swallow.producerserver.impl;

import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.monitor.collector.ProducerCollector;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;

/**
 * @author mengwenchao
 * 
 *         2015年4月18日 下午5:18:17
 */
public class AbstractProducerServer extends SimpleChannelUpstreamHandler {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected ProducerCollector producerCollector;

	protected MessageDAO messageDAO;
	
	/** topic的白名单 */
	protected TopicWhiteList topicWhiteList;

	public static final String producerServerIP = IPUtil
			.getFirstNoLoopbackIP4Address();

	public AbstractProducerServer() {
		
	}
	
	public AbstractProducerServer(MessageDAO messageDAO, TopicWhiteList topicWhiteList) {
		
		this.messageDAO = messageDAO;
		this.topicWhiteList = topicWhiteList;
	}

	public ProducerCollector getProducerCollector() {
		return producerCollector;
	}

	public void setProducerCollector(ProducerCollector producerCollector) {
		this.producerCollector = producerCollector;
	}

	public MessageDAO getMessageDAO() {
		return messageDAO;
	}

	public void setMessageDAO(MessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

	public TopicWhiteList getTopicWhiteList() {
		return topicWhiteList;
	}

	public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
