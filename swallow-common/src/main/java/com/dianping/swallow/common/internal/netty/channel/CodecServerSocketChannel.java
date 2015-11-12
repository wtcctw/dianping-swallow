package com.dianping.swallow.common.internal.netty.channel;

import java.nio.channels.SocketChannel;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.codec.Codec;

import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author mengwenchao
 *
 * 2015年8月28日 下午6:44:25
 */
public class CodecServerSocketChannel extends NioServerSocketChannel{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private Codec codec;
	
	public CodecServerSocketChannel(Codec codec){
		
		this.codec = codec;
	}
	
    @Override
    protected int doReadMessages(List<Object> buf) throws Exception {
        SocketChannel ch = javaChannel().accept();

        try {
            if (ch != null) {
                buf.add(createCodecChannel(ch));
                return 1;
            }
        } catch (Throwable t) {
            logger.warn("Failed to create a new channel from an accepted socket.", t);

            try {
                ch.close();
            } catch (Throwable t2) {
                logger.warn("Failed to close a socket.", t2);
            }
        }

        return 0;
    }

	private Object createCodecChannel(SocketChannel ch) {
		
		return new CodecSocketChannel(codec, this, ch);
	}
}
