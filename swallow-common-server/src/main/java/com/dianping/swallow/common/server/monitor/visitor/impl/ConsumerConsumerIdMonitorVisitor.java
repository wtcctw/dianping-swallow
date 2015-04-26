package com.dianping.swallow.common.server.monitor.visitor.impl;



import com.dianping.swallow.common.server.monitor.data.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;


/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:18:40
 */
public class ConsumerConsumerIdMonitorVisitor extends AbstractConsumerMonitorVisitor {
	
	private String consumerId;
	
	public ConsumerConsumerIdMonitorVisitor(String topic, String consumerId) {
		super(topic);
		this.consumerId = consumerId;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void visitTopic(TotalMap visitorData) {
		
		ConsumerTopicData consumerTopicData = (ConsumerTopicData) visitorData;
		
		ConsumerIdData result = new ConsumerIdData();
		result.setTotal();
		
		ConsumerIdData toMerge = consumerTopicData.get(consumerId);
		if(toMerge != null){
			result.merge(toMerge);
		}
		
		sendRawData.add(result.getTotalSendMessages());
		ackRawData.add(result.getTotalAckMessages());
	}

}
