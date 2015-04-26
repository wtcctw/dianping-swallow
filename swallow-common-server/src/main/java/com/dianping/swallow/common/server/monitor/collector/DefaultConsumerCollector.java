package com.dianping.swallow.common.server.monitor.collector;


import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.MonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:51:05
 */
public class DefaultConsumerCollector extends AbstractCollector implements ConsumerCollector{
	
	
	private ConsumerMonitorData consumerMonitorData = new ConsumerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
	
	@Override
	public void sendMessage(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message) {
		try{
			consumerMonitorData.addSendData(consumerInfo, consumerIp, message);
		}catch(Exception e){
			logger.error("[sendMessage]" + consumerInfo + "," + consumerIp + "," + message, e);
		}
	}

	@Override
	public void ackMessage(ConsumerInfo consumerInfo, String consumerIp, SwallowMessage message) {
		try{
			consumerMonitorData.addAckData(consumerInfo, consumerIp, message);
		}catch(Exception e){
			logger.error("[ackMessage]" + consumerInfo + "," + consumerIp + "," + message, e);
		}
	}

	@Override
	protected MonitorData getMonitorData() {
		return consumerMonitorData;
	}

	@Override
	protected String getServerType() {
		return "consumer";
	}
	
}
