package com.dianping.swallow.common.internal.heartbeat;

import org.jboss.netty.channel.Channel;

/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午11:08:57
 */
public class HeartBeat {
	
	private long 		  heartBeatTime;
	
	private Channel 	  channel;

	public HeartBeat(Channel channel) {
		
		this.channel = channel;
		heartBeatTime = System.currentTimeMillis();
		
	}


	public Long getHeartBeatTime(){
		
		return heartBeatTime;
	}


	public Channel getChannel() {
		return channel;
	}

}
