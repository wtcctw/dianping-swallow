package com.dianping.swallow.producerserver.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;

/**
 * @author mengwenchao
 * 
 *         2015年8月21日 上午11:35:47
 */
public class ProducerServerForText extends AbstractProducerServer {

	private static final int DEFAULT_PORT = 8000;
	private int port = DEFAULT_PORT;

	private MessageDAO messageDAO;

	private TopicWhiteList topicWhiteList;

	public ProducerServerForText() {
	}

	@Override
	protected void doStart() throws Exception {

		super.doStart();

		ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
				Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		bootstrap.setPipelineFactory(new ProducerServerTextPipelineFactory(messageDAO, topicWhiteList,
				producerCollector));
		bootstrap.bind(new InetSocketAddress(getPort()));

		if (logger.isInfoEnabled()) {
			logger.info("[Initialize netty sucessfully, Producer service for text is ready.]");
		}
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		
		
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMessageDAO(MessageDAO messageDAO) {
		this.messageDAO = messageDAO;
	}

}
