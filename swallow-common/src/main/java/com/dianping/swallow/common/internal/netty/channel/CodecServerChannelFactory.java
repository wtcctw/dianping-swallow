package com.dianping.swallow.common.internal.netty.channel;

import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.bootstrap.ChannelFactory;

/**
 * @author mengwenchao
 *
 * 2015年9月1日 下午3:53:28
 */
public class CodecServerChannelFactory implements ChannelFactory<CodecServerSocketChannel>{
	
	private Codec codec;
	
	public CodecServerChannelFactory(Codec codec){
		
		this.codec = codec;
	}

	@Override
	public CodecServerSocketChannel newChannel() {
		
		return new CodecServerSocketChannel(codec);
	}

}
