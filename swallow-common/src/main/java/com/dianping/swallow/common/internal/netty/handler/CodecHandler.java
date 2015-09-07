package com.dianping.swallow.common.internal.netty.handler;


import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author mengwenchao
 * 
 *         2015年8月25日 下午5:09:12
 */
public class CodecHandler extends ChannelDuplexHandler {
	
	private Codec codec;
	
	public CodecHandler(Codec codec) {
		
		this.codec = codec;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

		super.channelRead(ctx, decode(ctx, msg));
	}

	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

		super.write(ctx, encode(ctx, msg), promise);
	}

	protected Object decode(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (!(msg instanceof ByteBuf)) {
			return msg;
		}

		ByteBuf buf = (ByteBuf) msg;
		int messageLength = buf.readableBytes(); 
		
		byte []tmp  = new byte[messageLength];
		buf.readBytes(tmp, 0, messageLength);
		
		String json = new String(tmp,0, messageLength, Codec.DEFAULT_CHARSET);
		Object result = codec.decode(json);
		
		buf.release();
		return result;
	}

	protected Object encode(ChannelHandlerContext ctx, Object msg) throws Exception {

		if(msg instanceof ByteBuf){
			return msg;
		}
		
		return codec.encode(msg);
	}

}
