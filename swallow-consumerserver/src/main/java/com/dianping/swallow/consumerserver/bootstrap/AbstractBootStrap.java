package com.dianping.swallow.consumerserver.bootstrap;
	
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.dianping.swallow.common.internal.codec.Codec;
import com.dianping.swallow.common.internal.codec.impl.JsonCodec;
import com.dianping.swallow.common.internal.lifecycle.MasterSlaveComponent;
import com.dianping.swallow.common.internal.lifecycle.SelfManagement;
import com.dianping.swallow.common.internal.netty.channel.CodecServerChannelFactory;
import com.dianping.swallow.common.internal.netty.handler.CodecHandler;
import com.dianping.swallow.common.internal.netty.handler.LengthPrepender;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.netty.MessageServerHandler;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerWorkerManager;

/**
 * @author mengwenchao
 * 
 *         2015年2月27日 下午4:40:46
 */
public abstract class AbstractBootStrap {

	protected volatile boolean closed    = false;

	protected ServerBootstrap bootstrap;
	
	protected Map<String, MasterSlaveComponent> masterSlaveComponents;
	protected ConsumerWorkerManager consumerWorkerManager;
	protected TopicWhiteList topicWhiteList; 
    protected ConsumerAuthController consumerAuthController;
    protected Heartbeater heartbeater; 
    
    private EventLoopGroup bossGroup, workerGroup; 
    
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	static{
		   SwallowHelper.initialize();
	}

	protected ServerBootstrap startNetty(int port){

		if(logger.isInfoEnabled()){
	    	  logger.info("[startNetty][begin]" + port);
	    }

		ServerBootstrap bootstrap = new ServerBootstrap();
		
		final Codec jsonCodec = new JsonCodec(PktMessage.class, PktConsumerMessage.class);
		int workerCount = 4 * CommonUtils.getCpuCount();
		bossGroup = new NioEventLoopGroup(1, new MQThreadFactory("Swallow-Netty-Boss-"));
		workerGroup = new NioEventLoopGroup(workerCount, new MQThreadFactory("Swallow-Netty-Worker-"));

		bootstrap.channelFactory(new CodecServerChannelFactory(jsonCodec))
		.group(bossGroup, workerGroup)
		.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator(true, workerCount/2, workerCount/2, 8 * 1024, 10))
		.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				
				ChannelPipeline pipeline = ch.pipeline();
				MessageServerHandler handler = new MessageServerHandler(
						consumerWorkerManager, topicWhiteList,
						consumerAuthController);
				
				pipeline.addLast("frameDecoder",
						new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0,
								4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthPrepender());
				pipeline.addLast("jsonDecoder", new CodecHandler(jsonCodec));
				
				pipeline.addLast("handler", handler);
			}
		});

		try {
			bootstrap.bind(new InetSocketAddress(port)).sync();
		} catch (InterruptedException e) {
			logger.error("[startNetty]", e);
		}

		if(logger.isInfoEnabled()){
	    	  logger.info("[startNetty][Server started at port]" + port);
	    }
		System.out.println(DateUtils.current() + "[startNetty][Server started at port " + port + "]");
		
		
		return bootstrap;
	}

	protected void stopConsumerServer() {

		try {
			for(Entry<String, MasterSlaveComponent> entry : masterSlaveComponents.entrySet()){
				
				String name = entry.getKey();
				SelfManagement component = entry.getValue();
				if(logger.isInfoEnabled()){
					logger.info("[stopConsumerServer][stop and dispose]" + name);
				}
				component.stop();
				component.dispose();
			}
		} catch (Exception e) {
			logger.error("[error start worker]", e);
		}
	}

	protected void startConsumerServer() {
		try {
			for(Entry<String, MasterSlaveComponent> entry : masterSlaveComponents.entrySet()){
				
				String name = entry.getKey();
				SelfManagement component = entry.getValue();
				if(logger.isInfoEnabled()){
					logger.info("[startConsumerServer][init and start]" + name);
				}
				component.initialize();
				component.start();
			}
		} catch (Exception e) {
			logger.error("[error start worker]", e);
		}
	}

	protected void closeNettyRelatedResource() {
		
		if(logger.isInfoEnabled()){
			logger.info("[closeNettyRelatedResource]");
		}
		
		if(bossGroup != null){
			bossGroup.shutdownGracefully();
		}
		if(workerGroup != null){
			workerGroup.shutdownGracefully();
		}
			
	}

	protected void createShutdownHook() {
	      Thread hook = new Thread() {
		         @Override
		         public void run() {
		        	 closed = true;
	        		 stopConsumerServer();
	        		 closeNettyRelatedResource();
		         }
		      };
		      hook.setDaemon(true);
		      hook.setName("Swallow-ShutdownHook");
		      Runtime.getRuntime().addShutdownHook(hook);
	}

	protected void createContext() {
		
		ConfigManager.getInstance().setSlave(isSlave());
		
		@SuppressWarnings("resource")
		ApplicationContext ctx = new ClassPathXmlApplicationContext(
		            new String[] { "c-applicationContext.xml" });
		
	      consumerWorkerManager = ctx.getBean(ConsumerWorkerManager.class);
	      topicWhiteList = ctx.getBean(TopicWhiteList.class);
	      consumerAuthController = ctx.getBean(ConsumerAuthController.class);
		  heartbeater = ctx.getBean(Heartbeater.class);
	      
	      masterSlaveComponents = ctx.getBeansOfType(MasterSlaveComponent.class);
	}

	protected abstract boolean isSlave();
}
