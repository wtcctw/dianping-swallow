package com.dianping.swallow.common.internal.netty.channel;

import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.bootstrap.ChannelFactory;

/**
 * @author mengwenchao
 *
 * 2015年9月1日 下午3:53:28
 */
public class CodecChannelFactory implements ChannelFactory<CodecSocketChannel>{
	
	private Codec codec;
	
	public CodecChannelFactory(Codec codec){
		
		this.codec = codec;
	}

	@Override
	public CodecSocketChannel newChannel() {
		
		return new CodecSocketChannel(codec);
	}

}
