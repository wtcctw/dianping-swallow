package com.dianping.swallow.common.server.monitor.visitor;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午2:45:17
 */
public interface KeyBasedVisitor extends Visitor{
	
	void visit(Object result);
	
	String getNextKey();
	
	boolean hasNextKey();
	
}
