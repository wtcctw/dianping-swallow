package com.dianping.swallow.consumer.internal.netty;

import org.jboss.netty.channel.Channel;

/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午10:35:23
 */
public interface ConsumerConnectionListener {

	/**
	 * 连接建立
	 * @param channel 
	 */
	void onChannelConnected(Channel channel);
	
	/**
	 * 连接关闭
	 */
	void onChannelDisconnected(Channel channel);
	
}
