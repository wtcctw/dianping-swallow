package com.dianping.swallow.producerserver.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.common.server.monitor.collector.ProducerCollector;

public class ProducerServerTextHandler extends ChannelInboundHandlerAdapter {
    //TextHandler状态代码
    public static final int     OK                 = 250;
    public static final int     INVALID_TOPIC_NAME = 251;
    public static final int     SAVE_FAILED        = 252;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

    private MessageDAO messageDao;
    
    private TopicWhiteList topicWhiteList;
    
    private ProducerCollector producerCollector;
    
    
    public ProducerServerTextHandler(MessageDAO messageDAO, TopicWhiteList topicWhiteList, ProducerCollector producerCollector) {
    	
    	this.messageDao = messageDAO;
    	this.topicWhiteList = topicWhiteList;
    	this.producerCollector = producerCollector;
    }

    
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		Channel channel = ctx.channel();
		
        //获取TextObject
        TextObject textObject = (TextObject) msg;
        //生成SwallowMessage
        SwallowMessage swallowMessage = new SwallowMessage();
        swallowMessage.setContent(textObject.getContent());
        swallowMessage.setGeneratedTime(new Date());
        swallowMessage.setSha1(SHAUtil.generateSHA(swallowMessage.getContent()));
        swallowMessage.setSourceIp(IPUtil.getIpFromChannel(channel));

        //初始化ACK对象
        TextACK textAck = new TextACK();
        textAck.setStatus(OK);
        //TopicName非法，返回失败ACK，reason是"TopicName is not valid."
        String topicName = textObject.getTopic();
        if (!NameCheckUtil.isTopicNameValid(topicName)) {
            logger.error("[Incorrect topic name.][From=" + channel.remoteAddress() + "][Content=" + textObject + "]");
            textAck.setStatus(INVALID_TOPIC_NAME);
            textAck.setInfo("TopicName is invalid.");
            //返回ACK
            channel.writeAndFlush(textAck);
        } else {
            //验证topicName是否在白名单里
            boolean isValid = topicWhiteList.isValid(topicName);
            if (!isValid) {
                textAck.setStatus(INVALID_TOPIC_NAME);
                textAck.setInfo("Invalid topic(" + topicName + "), because it's not in whitelist, please contact swallow group for support.");
            } else {
                //调用DAO层将SwallowMessage存入DB
                try {
                    messageDao.saveMessage(topicName, swallowMessage);
                    producerCollector.addMessage(topicName, swallowMessage.getSourceIp(), 0, swallowMessage.getGeneratedTime().getTime(), System.currentTimeMillis());                    
                    textAck.setInfo(swallowMessage.getSha1());
                } catch (Exception e1) {
                    //记录异常，返回失败ACK，reason是“Can not save message”
                    logger.error("[Save message to DB failed.]", e1);
                    textAck.setStatus(SAVE_FAILED);
                    textAck.setInfo("Can not save message.");
                }
            }

            //如果不要ACK，立刻返回
            if (textObject.isACK()) {
                //返回ACK
                channel.writeAndFlush(textAck);
            }
        }
	}

	
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    	
        if (cause instanceof IOException) {
            ctx.channel().close();
        } else {
            logger.error("Unexpected exception from downstream.", cause);
        }
    }
}
