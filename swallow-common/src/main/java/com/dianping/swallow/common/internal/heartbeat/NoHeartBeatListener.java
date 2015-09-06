package com.dianping.swallow.common.internal.heartbeat;

import io.netty.channel.Channel;


/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午11:25:17
 */
public interface NoHeartBeatListener {
	
	void onNoHeartBeat(Channel channel);

}
