package com.dianping.swallow.common.internal.netty.channel;

import java.nio.channels.SocketChannel;

import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author mengwenchao
 *
 * 2015年8月28日 下午6:48:19
 */
public class CodecSocketChannel extends NioSocketChannel{
	
	private Codec codec;
	
	public CodecSocketChannel(Codec codec){
		this.codec = codec;
	}
	
	public CodecSocketChannel(Codec codec, Channel parent, SocketChannel socket) {
		
		super(parent, socket);
		this.codec = codec;
		
	}
	
    @Override
    public ChannelFuture write(Object msg) {
    	
        return super.write(encode(msg));
    }

	private Object encode(Object msg) {
		
		return codec.encode(msg);
	}

	@Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
    	
        return super.write(encode(msg), promise);
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg) {
    	
        return super.writeAndFlush(encode(msg));
    }

    @Override
    public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
    	
        return super.writeAndFlush(encode(msg), promise);
    }
}
