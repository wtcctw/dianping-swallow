package com.dianping.swallow.common.server.monitor.visitor.impl;



import com.dianping.swallow.common.server.monitor.data.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:18:40
 */
public class ConsumerTopicMonitorVisitor extends AbstractConsumerMonitorVisitor {

	public ConsumerTopicMonitorVisitor(String topic) {
		super(topic);
	}

	@Override
	public void visitTopic(@SuppressWarnings("rawtypes") TotalMap visitorData) {
		
		ConsumerTopicData consumerTopicData = (ConsumerTopicData)visitorData;

		
		ConsumerIdData consumerIdData = new ConsumerIdData();
		if(consumerTopicData != null){
			consumerIdData = consumerTopicData.getTotal();
		}
		
		sendRawData.add(consumerIdData.getTotalSendMessages());
		ackRawData.add(consumerIdData.getTotalAckMessages());
	}

}
