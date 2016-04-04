package com.dianping.swallow.consumerserver.worker;

import io.netty.channel.Channel;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerMessage;


/**
 * @author mengwenchao
 *
 * 2015年11月12日 下午5:07:02
 */
public interface SendAckManager extends Lifecycle, Observer{


	ConsumerMessage send();

	void exceptionWhileSending(ConsumerMessage consumerMessage, Throwable th);

	ConsumerMessage ack(Long messageId);

	void recordAck();


	void destClosed(Channel channel);

	Object getStatus();

}
