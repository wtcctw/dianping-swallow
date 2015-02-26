package com.dianping.swallow.consumerserver.bootstrap;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.codec.JsonDecoder;
import com.dianping.swallow.common.internal.codec.JsonEncoder;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.netty.MessageServerHandler;
import com.dianping.swallow.consumerserver.worker.ConsumerWorkerManager;

public class SlaveBootStrap {

   private static final Logger     logger       = LoggerFactory.getLogger(SlaveBootStrap.class);

   private static boolean          isSlave   = true;
   private static ServerBootstrap  bootstrap = null;
   private static volatile boolean closed    = false;

   private SlaveBootStrap() {
   }

   private static void closeNettyRelatedResource() {
      try {
         logger.info("MessageServerHandler.getChannelGroup().unbind()-started");
         MessageServerHandler.getChannelGroup().unbind().await();
         logger.info("MessageServerHandler.getChannelGroup().unbind()-finished");

         logger.info("MessageServerHandler.getChannelGroup().close()-started");
         MessageServerHandler.getChannelGroup().close().await();
         logger.info("MessageServerHandler.getChannelGroup().close()-finished");

         logger.info("MessageServerHandler.getChannelGroup().clear()-started");
         MessageServerHandler.getChannelGroup().clear();
         logger.info("MessageServerHandler.getChannelGroup().clear()-finished");

         logger.info("bootstrap.releaseExternalResources()-started");
         bootstrap.releaseExternalResources();
         logger.info("bootstrap.releaseExternalResources()-finished");
      } catch (InterruptedException e) {
         logger.error("Interrupted when closeNettyRelatedResource()", e);
         Thread.currentThread().interrupt();
      }
   }

   /**
    * 启动Slave
    */
   public static void main(String[] args) {
      //启动Cat
      Cat.initialize(new File("/data/appdatas/cat/client.xml"));

      ApplicationContext ctx = new ClassPathXmlApplicationContext(
            new String[] { "applicationContext-consumerserver.xml" });
      ConfigManager configManager = ConfigManager.getInstance();

      final ConsumerWorkerManager consumerWorkerManager = ctx.getBean(ConsumerWorkerManager.class);
      final TopicWhiteList topicWhiteList = ctx.getBean(TopicWhiteList.class);
      final ConsumerAuthController consumerAuthController = ctx.getBean(ConsumerAuthController.class);

      Heartbeater heartbeater = ctx.getBean(Heartbeater.class);

      Thread hook = new Thread() {
         @Override
         public void run() {
            closed = true;
            logger.info("consumerWorkerManager.close()-started");
            try {
				consumerWorkerManager.stop();
	            consumerWorkerManager.dispose();
			} catch (Exception e) {
			}
            logger.info("consumerWorkerManager.close()-finished");
            closeNettyRelatedResource();
         }
      };
      hook.setDaemon(true);
      hook.setName("Swallow-ShutdownHook");
      Runtime.getRuntime().addShutdownHook(hook);

      logger.info("slave starting, master ip: " + configManager.getMasterIp());
      consumerWorkerManager.isSlave(isSlave);

      while (!closed) {

         try {
            heartbeater.waitUntilMasterDown(configManager.getMasterIp(), configManager.getHeartbeatCheckInterval(),
                  configManager.getHeartbeatMaxStopTime());
         } catch (InterruptedException e) {
            logger.info("slave interruptted, will stop", e);
            break;
         }
         // master down, now start slave ...

         // start consumerWorkerManager
         try {
			consumerWorkerManager.initialize();
	         consumerWorkerManager.start();
         } catch (Exception e) {
        	 logger.error("[error start worker]", e);
         }

         // Configure the server.
         bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(),
               Executors.newCachedThreadPool()));

         bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
               MessageServerHandler handler = new MessageServerHandler(consumerWorkerManager, topicWhiteList, consumerAuthController);
               ChannelPipeline pipeline = Channels.pipeline();
               pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
               pipeline.addLast("jsonDecoder", new JsonDecoder(PktConsumerMessage.class));
               pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
               pipeline.addLast("jsonEncoder", new JsonEncoder(PktMessage.class));
               pipeline.addLast("handler", handler);
               return pipeline;
            }
         });

         // Bind and start to accept incoming connections.
         bootstrap.bind(new InetSocketAddress(ConfigManager.getInstance().getSlavePort()));
         logger.info("Server started on port " + ConfigManager.getInstance().getSlavePort());

         try {
            heartbeater.waitUntilMasterUp(configManager.getMasterIp(), configManager.getHeartbeatCheckInterval(),
                  configManager.getHeartbeatMaxStopTime());
         } catch (InterruptedException e) {
            logger.info("slave interruptted, will stop", e);
            break;
         }
         logger.info("consumerWorkerManager.close()-started");
         try {
        	 consumerWorkerManager.stop();
	         consumerWorkerManager.dispose();
		} catch (Exception e) {
			logger.error("[error close consumerWorkerManager]", e);
		};
         logger.info("consumerWorkerManager.close()-finished");
         closeNettyRelatedResource();
      }

      logger.info("slave stopped");
   }

}
