package com.dianping.swallow.common.internal.exception;

/**
 * 此异常打出，直接cat进行报警(慎用)
 * 注意：<br/>
 * <b>请直接使用此类，继承无效</b>
 * @author mengwenchao
 *
 * 2015年6月26日 下午2:47:24
 */
public class SwallowAlertException extends SwallowException{

	private static final long serialVersionUID = 1L;
	
	public SwallowAlertException(String message){
		super(message);
	}
	

}
