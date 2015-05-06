package com.dianping.swallow.consumerserver.bootstrap;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.LengthFieldBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.LengthFieldPrepender;
import org.jboss.netty.util.ThreadNameDeterminer;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.swallow.common.internal.codec.JsonDecoder;
import com.dianping.swallow.common.internal.codec.JsonEncoder;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.netty.MessageServerHandler;
import com.dianping.swallow.consumerserver.worker.ConsumerWorkerManager;

/**
 * @author mengwenchao
 * 
 *         2015年2月27日 下午4:40:46
 */
public abstract class AbstractBootStrap {

	protected volatile boolean closed    = false;

	protected ServerBootstrap bootstrap;
	
	protected ConsumerWorkerManager consumerWorkerManager;
	protected TopicWhiteList topicWhiteList; 
    protected ConsumerAuthController consumerAuthController;
    protected Heartbeater heartbeater; 
    
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	static{
		   SwallowHelper.initialize();
	}

	protected ServerBootstrap startNetty(int port) {

		if(logger.isInfoEnabled()){
	    	  logger.info("[startNetty][begin]" + port);
	    }

		ThreadRenamingRunnable.setThreadNameDeterminer(ThreadNameDeterminer.CURRENT);

		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(new MQThreadFactory("Swallow-Netty-Boss-")), 
						Executors.newCachedThreadPool(new MQThreadFactory("Swallow-Netty-Server-"))));

		bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			@Override
			public ChannelPipeline getPipeline() {
				MessageServerHandler handler = new MessageServerHandler(
						consumerWorkerManager, topicWhiteList,
						consumerAuthController);
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("frameDecoder",
						new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
								4, 0, 4));
				pipeline.addLast("jsonDecoder", new JsonDecoder(
						PktConsumerMessage.class));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				pipeline.addLast("jsonEncoder", new JsonEncoder(
						PktMessage.class));
				pipeline.addLast("handler", handler);
				return pipeline;
			}
		});

		bootstrap.bind(new InetSocketAddress(port));

		if(logger.isInfoEnabled()){
	    	  logger.info("[startNetty][Server started at port]" + port);
	    }
		System.out.println("[startNetty][Server started at port " + port + "]");
		
		
		return bootstrap;
	}

	protected void stopConsumerManager() {

		if(logger.isInfoEnabled()){
			logger.info("[stopConsumerManager][begin]");
		}
		try {
			consumerWorkerManager.stop();
			consumerWorkerManager.dispose();
		} catch (Exception e) {
			logger.error("[error close consumerWorkerManager]", e);
		}
		if(logger.isInfoEnabled()){
			logger.info("[stopConsumerManager][end]");
		}
	}

	protected void startConsumerWorkerManager() {
		if (logger.isInfoEnabled()) {
			logger.info("[startConsumerWorkerManager][begin]");
		}
		try {
			consumerWorkerManager.initialize();
			consumerWorkerManager.start();
		} catch (Exception e) {
			logger.error("[error start worker]", e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("[startConsumerWorkerManager][end]");
		}
	}

	protected void closeNettyRelatedResource() {
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

	protected void createShutdownHook() {
	      Thread hook = new Thread() {
		         @Override
		         public void run() {
		        	 closed = true;
	        		 stopConsumerManager();
	        		 closeNettyRelatedResource();
		         }
		      };
		      hook.setDaemon(true);
		      hook.setName("Swallow-ShutdownHook");
		      Runtime.getRuntime().addShutdownHook(hook);
	}

	protected void createContext() {
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
		            new String[] { "applicationContext-consumerserver.xml" });
	      consumerWorkerManager = ctx.getBean(ConsumerWorkerManager.class);
	      topicWhiteList = ctx.getBean(TopicWhiteList.class);
	      consumerAuthController = ctx.getBean(ConsumerAuthController.class);
		  heartbeater = ctx.getBean(Heartbeater.class);
	      consumerWorkerManager.isSlave(isSlave());

	}

	protected abstract boolean isSlave();
}
