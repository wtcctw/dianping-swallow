package com.dianping.swallow.common.internal.heartbeat;

import io.netty.channel.Channel;


/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午9:48:49
 */
public interface HeartBeatSender {
	
	public static final int HEART_BEAT_INTERVAL = 10;//seconds
	
	void addChannel(Channel channel);
	
	void removeChannel(Channel channel);
	

}
