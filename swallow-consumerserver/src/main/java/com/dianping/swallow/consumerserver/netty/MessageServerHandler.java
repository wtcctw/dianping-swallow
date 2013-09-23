package com.dianping.swallow.consumerserver.netty;

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
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.util.ConsumerIdUtil;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;
import com.dianping.swallow.consumerserver.worker.ConsumerWorkerManager;

public class MessageServerHandler extends SimpleChannelUpstreamHandler {

   private static final Logger   LOG          = LoggerFactory.getLogger(MessageServerHandler.class);

   private static ChannelGroup   channelGroup = new DefaultChannelGroup();

   private ConsumerWorkerManager workerManager;

   private ConsumerInfo          consumerInfo;

   private TopicWhiteList        topicWhiteList;

   private int                   clientThreadCount;

   private boolean               readyClose   = Boolean.FALSE;

   public MessageServerHandler(ConsumerWorkerManager workerManager, TopicWhiteList topicWhiteList) {
      this.workerManager = workerManager;
      this.topicWhiteList = topicWhiteList;
      LOG.info("Inited MessageServerHandler.");
   }

   @Override
   public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
      LOG.info(e.getChannel().getRemoteAddress() + " connected!");
      channelGroup.add(e.getChannel());
   }

   @Override
   public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {

      //收到PktConsumerACK，按照原流程解析
      final Channel channel = e.getChannel();
      if (e.getMessage() instanceof PktConsumerMessage) {
         PktConsumerMessage consumerPacket = (PktConsumerMessage) e.getMessage();
         if (ConsumerMessageType.GREET.equals(consumerPacket.getType())) {
            if (!NameCheckUtil.isTopicNameValid(consumerPacket.getDest().getName())) {
               LOG.error("TopicName(" + consumerPacket.getDest().getName() + ") inValid from "
                     + channel.getRemoteAddress());
               channel.close();
               return;
            }
            //验证topicName是否在白名单里
            boolean isValid = topicWhiteList.isValid(consumerPacket.getDest().getName());
            if (!isValid) {
               LOG.error("TopicName(" + consumerPacket.getDest().getName() + ") is not in whitelist, from "
                     + channel.getRemoteAddress());
               channel.close();
            }

            clientThreadCount = consumerPacket.getThreadCount();
            if (clientThreadCount > ConfigManager.getInstance().getMaxClientThreadCount()) {
               LOG.warn(channel.getRemoteAddress() + " with " + consumerInfo
                     + " clientThreadCount greater than MaxClientThreadCount("
                     + ConfigManager.getInstance().getMaxClientThreadCount() + ")");
               clientThreadCount = ConfigManager.getInstance().getMaxClientThreadCount();
            }
            String strConsumerId = consumerPacket.getConsumerId();
            if (strConsumerId == null || strConsumerId.trim().length() == 0) {
               consumerInfo = new ConsumerInfo(ConsumerIdUtil.getRandomNonDurableConsumerId(),
                     consumerPacket.getDest(), ConsumerType.NON_DURABLE);
            } else {
               if (!NameCheckUtil.isConsumerIdValid(consumerPacket.getConsumerId())) {
                  LOG.error("ConsumerId inValid from " + channel.getRemoteAddress());
                  channel.close();
                  return;
               }
               consumerInfo = new ConsumerInfo(strConsumerId, consumerPacket.getDest(),
                     consumerPacket.getConsumerType());
            }
            LOG.info("received greet from " + e.getChannel().getRemoteAddress() + " with " + consumerInfo);
            workerManager.handleGreet(channel, consumerInfo, clientThreadCount, consumerPacket.getMessageFilter());
         }
         if (ConsumerMessageType.ACK.equals(consumerPacket.getType())) {
            if (consumerPacket.getNeedClose() || readyClose) {
               //第一次接到channel的close命令后,server启一个后台线程,当一定时间后channel仍未关闭,则强制关闭.
               if (!readyClose) {
                  Thread thread = workerManager.getThreadFactory().newThread(new Runnable() {

                     @Override
                     public void run() {
                        try {
                           Thread.sleep(ConfigManager.getInstance().getCloseChannelMaxWaitingTime());
                        } catch (InterruptedException e) {
                           LOG.error("CloseChannelThread InterruptedException", e);
                        }
                        // channel.getRemoteAddress() 在channel断开后,不会抛异常
                        LOG.info("CloseChannelMaxWaitingTime reached, close channel " + channel.getRemoteAddress()
                              + " with " + consumerInfo);
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
      } else {
         LOG.warn("the received message is not PktConsumerMessage");
      }

   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) {
      removeChannel(e);
      LOG.error("Exception from " + e.getChannel().getRemoteAddress(), e.getCause());
      e.getChannel().close();

   }

   @Override
   public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
      removeChannel(e);
      super.channelDisconnected(ctx, e);
      LOG.info(e.getChannel().getRemoteAddress() + " disconnected!");
   }

   @Override
   public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
      removeChannel(e);
      e.getChannel().close();
      super.channelClosed(ctx, e);
      LOG.info(e.getChannel().getRemoteAddress() + " closed!");
   }

   private void removeChannel(ChannelEvent e) {
      channelGroup.remove(e.getChannel());
      if (consumerInfo != null) {//consumerInfo可能为null(比如未收到消息前，messageReceived未被调用，则consumerInfo未被初始化)
         Channel channel = e.getChannel();
         workerManager.handleChannelDisconnect(channel, consumerInfo);
      }
   }

   public static ChannelGroup getChannelGroup() {
      return channelGroup;
   }

}
