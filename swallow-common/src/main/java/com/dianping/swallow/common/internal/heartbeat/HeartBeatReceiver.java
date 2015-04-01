package com.dianping.swallow.common.internal.heartbeat;

import org.jboss.netty.channel.Channel;

/**
 * @author mengwenchao
 *
 * 2015年3月31日 下午11:21:30
 */
public interface HeartBeatReceiver {

	public static final int MAX_HEARTBEAT_INTERVAL_MULTI = 10;

	/**
	 * 取消超时检查
	 */
	void cancelCheck();

	/**
	 * @param channel
	 */
	void beat(Channel channel);

	/**
	 * 删除某个健康监测
	 * @param channel
	 */
	void remove(Channel channel);
}
