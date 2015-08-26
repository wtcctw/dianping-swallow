package com.dianping.swallow.common.internal.codec;

import java.nio.charset.Charset;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

/**
 * @author mengwenchao
 * 
 *         2015年8月25日 下午5:09:12
 */
public class JsonCoder extends ChannelDuplexHandler {

	private Class<?> encodeClazz, decodeClazz;

	public JsonCoder(Class<?> encoderClazz, Class<?> decodeClazz) {

		this.encodeClazz = encoderClazz;
		this.decodeClazz = decodeClazz;
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
		String json = buf.toString(Charset.forName("UTF-8"));
		JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
		return jsonBinder.fromJson(json, decodeClazz);
	}

	protected Object encode(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		if (msg.getClass() == encodeClazz) {// 对Message进行编码
			JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();
			String json = jsonBinder.toJson(msg);
			byte[] jsonBytes = json.getBytes(Charset.forName("UTF-8"));
			
			ByteBuf buff = ctx.alloc().buffer(jsonBytes.length);
			buff.writeBytes(jsonBytes);
			return buff;
		}
		return msg;
	}

}
