package com.dianping.swallow.common.internal.netty.handler;

import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @author mengwenchao
 *
 * 2015年9月1日 下午6:15:55
 */
public class LengthPrepender extends ChannelOutboundHandlerAdapter{

	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		
		
		ByteBuf result = null;
		if(msg instanceof byte[]){
			result = processBytes(ctx.alloc(), (byte[])msg);
		}else if(msg instanceof String){
			result = processBytes(ctx.alloc(), ((String)msg).getBytes(Codec.DEFAULT_CHARSET));
		}else if(msg instanceof ByteBuf){
			result = processByteBuf(ctx.alloc(), (ByteBuf)msg);
		}else{
			throw new IllegalArgumentException("not supported messagetype " + msg.getClass());
		}

		super.write(ctx, result, promise);
	}

	private ByteBuf processByteBuf(ByteBufAllocator alloc, ByteBuf rawMessage) {

		if(rawMessage.readableBytes() < 0){
			throw new IllegalStateException("message length:" + rawMessage.readableBytes());
		}

		ByteBuf buff = alloc.ioBuffer(rawMessage.readableBytes() + 4);
		buff.writeInt(rawMessage.readableBytes());
		buff.writeBytes(rawMessage);
		
		return buff;
	}

	private ByteBuf processBytes(ByteBufAllocator byteBufAllocator, byte[] rawMessage) {
		
		if(rawMessage.length < 0){
			throw new IllegalStateException("message length:" + rawMessage.length);
		}
		
		ByteBuf buff = byteBufAllocator.ioBuffer(rawMessage.length + 4);
		buff.writeInt(rawMessage.length);
		buff.writeBytes(rawMessage);
		return buff;
	}
}
