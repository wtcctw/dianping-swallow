package com.dianping.swallow.common.server.monitor.visitor.impl;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午2:56:56
 */
public class GetResultVisitor extends AbstractKeyBasedVisitor{

	
	private Object result = null;

	public GetResultVisitor(String ...keys){
		super(keys);
	}
	
	public GetResultVisitor(String key) {
		super(key);
	}

	@Override
	public void visit(Object result) {

		this.result = result;
	}
	
	public Object getResult() {
		
		return result;
	}

}
