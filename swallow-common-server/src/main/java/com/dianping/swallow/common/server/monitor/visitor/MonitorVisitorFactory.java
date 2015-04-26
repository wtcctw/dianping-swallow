package com.dianping.swallow.common.server.monitor.visitor;

import com.dianping.swallow.common.server.monitor.visitor.impl.ConsumerConsumerIdMonitorVisitor;
import com.dianping.swallow.common.server.monitor.visitor.impl.ProducerTopicMonitorVisitor;

/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午11:15:07
 */
public class MonitorVisitorFactory {
	
	public static ProducerMonitorVisitor buildProducerTopicVisitor(String topic){
		
		return new ProducerTopicMonitorVisitor(topic);
	}

	
	public static ConsumerMonitorVisitor buildConsumerConsumerIdVisitor(String topic, String consumerId){
		
		return new ConsumerConsumerIdMonitorVisitor(topic, consumerId);
		
	}
}
