package com.dianping.swallow.common.server.monitor.collector;


import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.monitor.data.MonitorData;
import com.dianping.swallow.common.server.monitor.data.ProducerMonitorData;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:56:21
 */
public class DefaultProducerCollector extends AbstractCollector implements ProducerCollector{
	
	private ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
	
	@Override
	public void addMessage(String topic, String producerIp, long messageId, long sendTime,
			long saveTime) {
		try{
			producerMonitorData.addData(topic, producerIp, messageId, sendTime, saveTime);
		}catch(Exception e){
			logger.error("[addMessage]" + topic + "," + messageId, e);
		}
	}

	@Override
	protected MonitorData getMonitorData() {
		
		return producerMonitorData;
	}

	@Override
	protected String getServerType() {
		
		return "producer";
	}
	
	

}
