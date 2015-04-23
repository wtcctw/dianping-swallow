package com.dianping.swallow.common.server.monitor.visitor.impl;

import java.util.Map.Entry;

import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.ConsumerMonitorData.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.data.MonitorData;


/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:18:40
 */
public class ConsumerTopicMonitorVisitor extends AbstractConsumerMonitorVisitor {
	
	public ConsumerTopicMonitorVisitor(String topic) {
		super(topic);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void visitTopic(TotalMap visitorData) {
		
		ConsumerTopicData consumerTopicData = (ConsumerTopicData) visitorData;
		
		ConsumerIdData result = new ConsumerIdData();
		
		for(Entry<String, ConsumerIdData> entry : consumerTopicData.entrySet()){
			ConsumerIdData consumerIdData = entry.getValue();
			result.merge(MonitorData.TOTAL_KEY, consumerIdData);
		}
		
		sendRawData.add(result.getTotalSendMessages());
		ackRawData.add(result.getTotalAckMessages());
	}

}
