package com.dianping.swallow.producerserver.impl;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.producerserver.AbstractProducerServerTest;

public class ProducerServerForTextTest extends AbstractProducerServerTest{
	
	private BlockingQueue<TextACK> resultQueue = new LinkedBlockingQueue<TextACK>();
	
    @Test
    public void testProducerServerForText() throws Exception {
    	
    	
        //构造mock的文本对象
        final TextObject textObj = new TextObject();
        textObj.setACK(true);
        textObj.setContent("This is a Mock Text content.");
        textObj.setTopic(topicName);

        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 8000);

        
        Channel channel = mockChannel(socketAddress, textObj);
        ChannelHandlerContext ctx = mockChannelHandlerContext(socketAddress, channel);
        
        
        ProducerServerTextHandler producerServerTextHandler = new ProducerServerTextHandler(messageReceiver);

        producerServerTextHandler.channelRead(ctx, textObj);
        
        TextACK ack = resultQueue.poll(1, TimeUnit.SECONDS);
        Assert.assertEquals(ProducerServerTextHandler.OK, ack.getStatus());
        
        replaceExceptionDao();
        
        producerServerTextHandler.channelRead(ctx, textObj);
        
        ack = resultQueue.poll(1, TimeUnit.SECONDS);
        Assert.assertEquals(ProducerServerTextHandler.SAVE_FAILED, ack.getStatus());

    }

	private Channel mockChannel(SocketAddress socketAddress, final TextObject textObj) {
		
		Channel channel = mock(Channel.class);
        when(channel.remoteAddress()).thenReturn(socketAddress);
        when(channel.writeAndFlush(argThat(new Matcher<TextACK>() {
            @Override
            public void describeTo(Description arg0) {
            }

            @Override
            public void _dont_implement_Matcher___instead_extend_BaseMatcher_() {
            }

            @Override
            public boolean matches(Object arg0) {
                TextACK textAck = (TextACK) arg0;
                Assert.assertEquals(TextACK.class, arg0.getClass());
                
            	try {
					resultQueue.put(textAck);
				} catch (InterruptedException e) {
					
				}

            	return true;
            }

//			@Override
			public void describeMismatch(Object item,
					Description mismatchDescription) {
				
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
