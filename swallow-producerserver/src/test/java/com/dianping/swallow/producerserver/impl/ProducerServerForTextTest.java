package com.dianping.swallow.producerserver.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;

import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.common.server.monitor.collector.DefaultProducerCollector;

public class ProducerServerForTextTest {
	
	private String topicName = "UnitTest";
	
    @Test
    public void testProducerServerForText() throws Exception {
    	
    	
        //构造mock的文本对象
        final TextObject textObj = new TextObject();
        textObj.setACK(true);
        textObj.setContent("This is a Mock Text content.");
        textObj.setTopic(topicName);

        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8000);

        MessageDAO messageDAO = mock(MessageDAO.class);
        Channel channel = mockChannel(socketAddress, textObj);
        ChannelHandlerContext ctx = mockChannelHandlerContext(socketAddress, channel);
        TopicWhiteList whiteList = new TopicWhiteList();
        whiteList.addTopic(topicName);
        
        
        ProducerServerTextHandler producerServerTextHandler = new ProducerServerTextHandler(messageDAO, whiteList, new DefaultProducerCollector());

        //测试发送消息
        producerServerTextHandler.channelRead(ctx, textObj);

        doThrow(new RuntimeException()).when(messageDAO).saveMessage(Matchers.anyString(), (SwallowMessage) Matchers.anyObject());
        producerServerTextHandler.channelRead(ctx, textObj);

        new ProducerServerForText().start();
    }

	private Channel mockChannel(SocketAddress socketAddress, final TextObject textObj) {
		
		Channel channel = mock(Channel.class);
        when(channel.remoteAddress()).thenReturn(socketAddress);
        when(channel.write(argThat(new Matcher<TextACK>() {
            @Override
            public void describeTo(Description arg0) {
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
            }

            @Override
            public boolean matches(Object arg0) {
                TextACK textAck = (TextACK) arg0;
                System.out.println(textAck.toString());
                Assert.assertEquals(TextACK.class, arg0.getClass());
                switch (textAck.getStatus()) {
                    case ProducerServerTextHandler.OK:
                        Assert.assertEquals(SHAUtil.generateSHA(textObj.getContent()), textAck.getInfo());
                        break;
                    case ProducerServerTextHandler.INVALID_TOPIC_NAME:
//                        Assert.assertTrue(textAck.getInfo().indexOf("Invalid") != -1);
                        break;
                    case ProducerServerTextHandler.SAVE_FAILED:
                        Assert.assertEquals("Can not save message.", textAck.getInfo());
                        break;
                }
                return true;
            }

//			@Override
			public void describeMismatch(Object item,
					Description mismatchDescription) {
				// TODO Auto-generated method stub
				
			}
        }))).thenReturn(null);
        return channel;
	}

	private ChannelHandlerContext mockChannelHandlerContext(SocketAddress socketAddress, Channel channel) {
		
		ChannelHandlerContext ctx = mock(ChannelHandlerContext.class);
		
		when(ctx.channel()).thenReturn(channel);
		
		return ctx;
	}

}
