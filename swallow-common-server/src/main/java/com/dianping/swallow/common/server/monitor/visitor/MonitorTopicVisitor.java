package com.dianping.swallow.common.server.monitor.visitor;

import com.dianping.swallow.common.server.monitor.data.structure.TotalMap;

/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:07:15
 */
public interface MonitorTopicVisitor extends Visitor{
	

	String getVisitTopic();
		
	void visitTopic(@SuppressWarnings("rawtypes") TotalMap visitorData);

}
