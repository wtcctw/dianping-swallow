package com.dianping.swallow.common.internal.monitor.collector;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.monitor.data.ConsumerMonitorData;
import com.dianping.swallow.common.internal.util.IPUtil;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:51:05
 */
public class DefaultConsumerCollector extends AbstractCollector implements ConsumerCollector{
	
	
	private ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
	
	@Override
	public void sendMessage(ConsumerInfo consumerInfo, SwallowMessage message) {
		
		consumerMonitorData.addSendData(consumerInfo, message);
	}

	@Override
	public void ackMessage(ConsumerInfo consumerInfo, SwallowMessage message) {
		
		consumerMonitorData.addAckData(consumerInfo, message);
	}
	
}
