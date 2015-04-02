package com.dianping.swallow.consumerserver.netty;

import java.net.InetSocketAddress;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.consumer.ConsumerMessageType;
import com.dianping.swallow.common.internal.packet.PacketType;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.util.ConsumerIdUtil;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.util.ConsumerUtil;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;
import com.dianping.swallow.consumerserver.worker.ConsumerWorkerManager;

public class MessageServerHandler extends SimpleChannelUpstreamHandler {

	private static final Logger logger = LoggerFactory.getLogger(MessageServerHandler.class);

	private static ChannelGroup channelGroup = new DefaultChannelGroup();

	private ConsumerWorkerManager workerManager;

	private ConsumerInfo consumerInfo;

	private ConsumerAuthController consumerAuthController;

	private TopicWhiteList topicWhiteList;

	private int clientThreadCount;

	private boolean readyClose = Boolean.FALSE;

	public MessageServerHandler(ConsumerWorkerManager workerManager, TopicWhiteList topicWhiteList,
			ConsumerAuthController consumerAuthController) {
		this.workerManager = workerManager;
		this.topicWhiteList = topicWhiteList;
		this.consumerAuthController = consumerAuthController;
		if (logger.isInfoEnabled()) {
			logger.info("Inited MessageServerHandler.");
		}
	}

	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
		if (logger.isInfoEnabled()) {
			logger.info(e.getChannel().getRemoteAddress() + " connected!");
		}
		channelGroup.add(e.getChannel());
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

		final Channel channel = e.getChannel();
		if (!(e.getMessage() instanceof PktConsumerMessage)) {
			logger.warn("the received message is not PktConsumerMessage");
			return;
		}

		PktConsumerMessage consumerPacket = (PktConsumerMessage) e.getMessage();

		
		if (ConsumerMessageType.GREET.equals(consumerPacket.getType()) || PacketType.CONSUMER_GREET.equals(consumerPacket.getPacketType())) {//兼容老版本
			handleGreet(channel, consumerPacket);
		} else if (ConsumerMessageType.ACK.equals(consumerPacket.getType()) || PacketType.CONSUMER_ACK.equals(consumerPacket.getPacketType())) {//兼容老版本
			handleAck(channel, consumerPacket);
		}else if(PacketType.HEART_BEAT.equals(consumerPacket.getPacketType())){
			handleHeartBeat(channel, consumerPacket);
		}

	}

	private void handleHeartBeat(Channel channel, PktConsumerMessage consumerPacket) {
		workerManager.handleHeartBeat(channel, consumerInfo);
	}

	private void handleAck(final Channel channel, PktConsumerMessage consumerPacket) {
		if (consumerPacket.getNeedClose() || readyClose) {
			// 第一次接到channel的close命令后,server启一个后台线程,当一定时间后channel仍未关闭,则强制关闭.
			if (!readyClose) {
				Thread thread = workerManager.getThreadFactory().newThread(new Runnable() {

					@Override
					public void run() {
						try {
							Thread.sleep(ConfigManager.getInstance().getCloseChannelMaxWaitingTime());
						} catch (InterruptedException e) {
							logger.error(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel)
									+ " CloseChannelThread InterruptedException", e);
						}
						// channel.getRemoteAddress() 在channel断开后,不会抛异常
						if (logger.isInfoEnabled()) {
							logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel)
									+ " CloseChannelMaxWaitingTime reached, close channel.");
						}
						channel.close();
						workerManager.handleChannelDisconnect(channel, consumerInfo);
					}
				}, consumerInfo.toString() + "-CloseChannelThread-");
				thread.setDaemon(true);
				thread.start();
			}
			clientThreadCount--;
			readyClose = Boolean.TRUE;
		}
		ACKHandlerType handlerType = null;
		if (readyClose && clientThreadCount == 0) {
			handlerType = ACKHandlerType.CLOSE_CHANNEL;
		} else if (readyClose && clientThreadCount > 0) {
			handlerType = ACKHandlerType.NO_SEND;
		} else if (!readyClose) {
			handlerType = ACKHandlerType.SEND_MESSAGE;
		}
		workerManager.handleAck(channel, consumerInfo, consumerPacket.getMessageId(), handlerType);
		
	}

	private void handleGreet(Channel channel, PktConsumerMessage consumerPacket) {
		String strConsumerId = consumerPacket.getConsumerId();
		if (strConsumerId == null || strConsumerId.trim().length() == 0) {
			consumerInfo = new ConsumerInfo(ConsumerIdUtil.getRandomNonDurableConsumerId(), consumerPacket.getDest(),
					ConsumerType.NON_DURABLE);
		} else {
			if (!NameCheckUtil.isConsumerIdValid(consumerPacket.getConsumerId())) {
				if (logger.isInfoEnabled()) {
					logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel) + "ConsumerId inValid.");
				}
				channel.close();
				return;
			}
			consumerInfo = new ConsumerInfo(strConsumerId, consumerPacket.getDest(), consumerPacket.getConsumerType());
		}
		// Topic
		if (!NameCheckUtil.isTopicNameValid(consumerInfo.getDest().getName())) {
			if (logger.isInfoEnabled()) {
				logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel) + " TopicName inValid.");
			}
			channel.close();
			return;
		}
		// 验证topicName是否在白名单里（白名单的控制，只在greet时检查，已经连接上的，不检查）
		boolean isValid = topicWhiteList.isValid(consumerInfo.getDest().getName());
		if (!isValid) {
			if (logger.isInfoEnabled()) {
				logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel)
						+ " TopicName is not in whitelist.");
			}
			channel.close();
			return;
		}
		// 验证该消费者是否被合法
		boolean isAuth = consumerAuthController.isValid(consumerInfo, ((InetSocketAddress) channel.getRemoteAddress())
				.getAddress().getHostAddress());
		if (!isAuth) {
			if (logger.isInfoEnabled()) {
				logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel) + " Consumer is disabled.");
			}
			channel.close();
			return;
		}

		clientThreadCount = consumerPacket.getThreadCount();
		if (clientThreadCount > ConfigManager.getInstance().getMaxClientThreadCount()) {
			if (logger.isInfoEnabled()) {
				logger.warn(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel)
						+ " ClientThreadCount greater than MaxClientThreadCount("
						+ ConfigManager.getInstance().getMaxClientThreadCount() + ")");
			}
			clientThreadCount = ConfigManager.getInstance().getMaxClientThreadCount();
		}

		if (logger.isInfoEnabled()) {
			logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel) + ", threadCount:"
					+ clientThreadCount + " Received greet.");
		}
		workerManager.handleGreet(channel, consumerInfo, clientThreadCount, consumerPacket.getMessageFilter(),
				consumerPacket.getMessageId() == null ? -1 : consumerPacket.getMessageId());

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
		removeChannel(e);
		logger.error(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, e.getChannel())
				+ " ExceptionCaught, channel will be close.", e.getCause());
		e.getChannel().close();

	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		removeChannel(e);
		super.channelDisconnected(ctx, e);
		if (logger.isInfoEnabled()) {
			logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, e.getChannel()) + " Disconnected!");
		}
	}

	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
		removeChannel(e);
		e.getChannel().close();
		super.channelClosed(ctx, e);
		if (logger.isInfoEnabled()) {
			logger.info(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, e.getChannel()) + " Channel closed.");
		}
	}

	private void removeChannel(ChannelEvent e) {
		channelGroup.remove(e.getChannel());
		if (consumerInfo != null) {// consumerInfo可能为null(比如未收到消息前，messageReceived未被调用，则consumerInfo未被初始化)
			Channel channel = e.getChannel();
			workerManager.handleChannelDisconnect(channel, consumerInfo);
		}
	}

	public static ChannelGroup getChannelGroup() {
		return channelGroup;
	}

}
