package com.dianping.swallow.common.server.monitor.visitor.impl;

import java.util.HashSet;
import java.util.Set;

import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;
import com.dianping.swallow.common.server.monitor.visitor.MonitorVisitor;

/**
 * @author mengwenchao
 *
 * 2015年4月24日 下午11:34:27
 */
public class TopicCollector implements MonitorVisitor{

	private Set<String>  topics = new HashSet<String>();
	@Override
	public void visit(String topic, TotalMap<?> all) {
		topics.add(topic);
	}
	
	public Set<String> getTopics(){
		
		return topics;
	}
}

