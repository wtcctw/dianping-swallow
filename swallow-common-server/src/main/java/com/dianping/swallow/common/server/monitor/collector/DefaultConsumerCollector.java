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
	public void sendMessage(ConsumerInfo consumerInfo, String consumerIpPort, SwallowMessage message) {
		
		String topic = consumerInfo.getDest().getName();
		
		if(isExclude(topic)){
			return;
		}
		
		try{
			consumerMonitorData.addSendData(consumerInfo, consumerIpPort, message);
		}catch(Exception e){
			logger.error("[sendMessage]" + consumerInfo + "," + consumerIpPort + "," + message, e);
		}
	}


	@Override
	public void ackMessage(ConsumerInfo consumerInfo, String consumerIpPort, SwallowMessage message) {

		String topic = consumerInfo.getDest().getName();
		
		if(isExclude(topic)){
			return;
		}
		try{
			consumerMonitorData.addAckData(consumerInfo, consumerIpPort, message);
		}catch(Exception e){
			logger.error("[ackMessage]" + consumerInfo + "," + consumerIpPort + "," + message, e);
		}
	}

	@Override
	protected MonitorData getMonitorData() {
		consumerMonitorData.buildTotal();
		return consumerMonitorData;
	}

	@Override
	protected String getServerType() {
		return "consumer";
	}
	
}
