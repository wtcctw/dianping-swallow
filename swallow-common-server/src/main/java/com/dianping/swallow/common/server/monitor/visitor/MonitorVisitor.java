package com.dianping.swallow.common.server.monitor.visitor;

import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年4月24日 下午11:33:49
 */
public interface MonitorVisitor extends Visitor{
	
	void visit(String topic, TotalMap<?> monitorData);

}
