package com.dianping.swallow.producerserver.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;

public class ProducerServerForText {
   private static final int    DEFAULT_PORT = 8000;
   private int                 port         = DEFAULT_PORT;
   private static final Logger LOGGER       = LoggerFactory.getLogger(ProducerServerForText.class);
   private MessageDAO          messageDAO;
   /** topic的白名单 */
   private TopicWhiteList      topicWhiteList;

   public ProducerServerForText() {
   }

   public void start() {
      ServerBootstrap bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(
            Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
      bootstrap.setPipelineFactory(new ProducerServerTextPipelineFactory(messageDAO, topicWhiteList));
      bootstrap.bind(new InetSocketAddress(getPort()));
      LOGGER.info("[Initialize netty sucessfully, Producer service for text is ready.]");
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
