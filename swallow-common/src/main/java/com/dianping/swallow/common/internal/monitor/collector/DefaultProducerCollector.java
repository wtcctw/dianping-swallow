package com.dianping.swallow.common.internal.monitor.collector;

import com.dianping.swallow.common.internal.monitor.data.ProducerMonitorData;
import com.dianping.swallow.common.internal.util.IPUtil;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 上午11:56:21
 */
public class DefaultProducerCollector extends AbstractCollector implements ProducerCollector{
	
	private ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
	
	@Override
	public void addMessage(String topic, long messageId, long sendTime,
			long saveTime) {
		
		producerMonitorData.addData(topic, messageId, sendTime, saveTime);
	}

}
