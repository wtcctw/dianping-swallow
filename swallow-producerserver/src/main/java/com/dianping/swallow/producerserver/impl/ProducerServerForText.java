package com.dianping.swallow.producerserver.impl;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

import java.net.InetSocketAddress;

import com.dianping.swallow.common.internal.codec.Codec;
import com.dianping.swallow.common.internal.codec.impl.JsonCodec;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.netty.channel.CodecServerChannelFactory;
import com.dianping.swallow.common.internal.netty.handler.CodecHandler;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
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
	
	private NioEventLoopGroup bossGroup;
	
	private NioEventLoopGroup workerGroup;
	

	public ProducerServerForText() {
	}

	@Override
	protected void doStart() throws Exception {

		super.doStart();

		ServerBootstrap bootstrap = new ServerBootstrap();
		
		bossGroup = new NioEventLoopGroup(1, new MQThreadFactory("SWALLOW_SERVER_BOSS"));
		workerGroup = new NioEventLoopGroup(1, new MQThreadFactory("SWALLOW_SERVER_WORKER"));
		
		final Codec codec = new JsonCodec(TextACK.class, TextObject.class);
		
		bootstrap.group(bossGroup, workerGroup)
		.channelFactory(new CodecServerChannelFactory(codec))
		.childOption(ChannelOption.ALLOCATOR, new PooledByteBufAllocator())
		.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {

			      ChannelPipeline pipeline = ch.pipeline();

			      pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
			      pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
			      
			      pipeline.addLast("jsonDecoder", new CodecHandler(codec));

			      pipeline.addLast("handler", new ProducerServerTextHandler(messageDAO, topicWhiteList, producerCollector));


			}
		});
		
		bootstrap.bind(new InetSocketAddress(getPort())).sync();

		if (logger.isInfoEnabled()) {
			logger.info("[Initialize netty sucessfully, Producer service for text is ready.]");
		}
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		
		if(bossGroup != null){
			bossGroup.shutdownGracefully();
		}
		if(workerGroup != null){
			workerGroup.shutdownGracefully();
		}
		
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
