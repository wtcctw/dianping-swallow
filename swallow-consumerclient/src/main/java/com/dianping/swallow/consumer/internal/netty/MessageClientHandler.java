package com.dianping.swallow.consumer.internal.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.channels.ClosedChannelException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.consumer.internal.ConsumerImpl;
import com.dianping.swallow.consumer.internal.task.DefaultConsumerTask;
import com.dianping.swallow.consumer.internal.task.TaskChecker;

/**
 * <em>Internal-use-only</em> used by Swallow. <strong>DO NOT</strong> access
 * this class outside of Swallow.
 *
 * @author zhang.yu
 */
public class MessageClientHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LogManager.getLogger(MessageClientHandler.class);

    private final ConsumerImpl consumer;
    private ConsumerProcessor processor;
    private SwallowCatActionWrapper actionWrapper;
    private TaskChecker taskChecker;
    private ConsumerConnectionListener consumerConnectionListener;

    public MessageClientHandler(ConsumerImpl consumer, ConsumerProcessor processor, TaskChecker taskChecker, SwallowCatActionWrapper actionWrapper,
                                ConsumerConnectionListener consumerConnectionListener) {

        this.consumer = consumer;
        this.processor = processor;
        this.actionWrapper = actionWrapper;
        this.taskChecker = taskChecker;
        this.consumerConnectionListener = consumerConnectionListener;
    }

    @Override
    public void channelActive(io.netty.channel.ChannelHandlerContext ctx) throws Exception {

        consumerConnectionListener.onChannelConnected(ctx.channel());

        if (logger.isInfoEnabled()) {
            logger.info("[channelActive]" + ctx.channel());
        }

        PktConsumerMessage consumerMessage = new PktConsumerMessage(consumer.getConsumerId(),
                consumer.getDest(), consumer.getConfig().getConsumerType(), consumer.getConfig().getThreadPoolSize(),
                consumer.getConfig().getMessageFilter());

        consumerMessage.setMessageId(consumer.getConfig().getStartMessageId());

        if (logger.isInfoEnabled()) {
            logger.info("[channelActive] " + consumer.toString());
        }

        ctx.channel().writeAndFlush(consumerMessage);
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (logger.isDebugEnabled()) {
            logger.debug("[channelRead]" + ctx.channel());
        }

        //如果已经close，接收到消息时，不回复ack，而是关闭连接。
        if (consumer.isClosed()) {
            logger.info("[channelRead]Message receiced, but it was rejected because consumer was closed.");
            ctx.channel().close();
            return;
        }

        this.consumer.submit(new DefaultConsumerTask(ctx, (PktMessage) msg, consumer, processor, actionWrapper, taskChecker));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        Channel channel = ctx.channel();
        logger.error("[exceptionCaught]" + channel, cause);

        if (cause instanceof ClosedChannelException) {
            consumerConnectionListener.onChannelDisconnected(channel);
        }
        channel.close();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

        if (logger.isInfoEnabled()) {
            logger.info("[channelInactive]" + ctx.channel());
        }
        consumerConnectionListener.onChannelDisconnected(ctx.channel());
    }

}
