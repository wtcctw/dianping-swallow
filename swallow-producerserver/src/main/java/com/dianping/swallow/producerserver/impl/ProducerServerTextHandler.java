package com.dianping.swallow.producerserver.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.IOException;
import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.producerserver.MessageReceiver;
import com.dianping.swallow.producerserver.MessageReceiver.VALID_STATUS;

public class ProducerServerTextHandler extends ChannelInboundHandlerAdapter {
    //TextHandler状态代码
    public static final int     OK                 = 250;
    public static final int     INVALID_TOPIC_NAME = 251;
    public static final int     SAVE_FAILED        = 252;

	protected final Logger logger = LogManager.getLogger(getClass());

    private MessageReceiver messageReceiver;
    
    public ProducerServerTextHandler(MessageReceiver messageReceiver) {
    	
    	this.messageReceiver = messageReceiver;
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

        TextACK textAck = new TextACK();
        textAck.setStatus(OK);
        
        String topicName = textObject.getTopic();

        
        VALID_STATUS validStatus = messageReceiver.isTopicNameValid(topicName);
        switch(validStatus){
        
        	case SUCCESS:
        		break;
        		
        	case TOPIC_NAME_INVALID:
        		
                logger.error("[Incorrect topic name.][From=" + channel.remoteAddress() + "][Content=" + textObject + "][invalid]");
        		textAck.setStatus(INVALID_TOPIC_NAME);
                textAck.setInfo("TopicName is invalid.");
                
        	case TOPIC_NAME_NOT_IN_WHITELIST:
        		
                logger.error("[Incorrect topic name.][From=" + channel.remoteAddress() + "][Content=" + textObject + "][not in whitelist]");
                
                textAck.setStatus(INVALID_TOPIC_NAME);
                channel.writeAndFlush(textAck);
        	default:
                logger.error("[Incorrect topic name.][From=" + channel.remoteAddress() + "][Content=" + textObject + "][unknown state]" + validStatus);
                textAck.setStatus(INVALID_TOPIC_NAME);
                channel.writeAndFlush(textAck);
                return;
        }
        		
        //success
        try {
            messageReceiver.receiveMessage(topicName, null, swallowMessage);
            textAck.setInfo(swallowMessage.getSha1());
        } catch (Exception e1) {
            logger.error("[Save message to DB failed.]", e1);
            textAck.setStatus(SAVE_FAILED);
            textAck.setInfo("Can not save message.");
        }
            
        if (textObject.isACK()) {
            //返回ACK
            channel.writeAndFlush(textAck);
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
