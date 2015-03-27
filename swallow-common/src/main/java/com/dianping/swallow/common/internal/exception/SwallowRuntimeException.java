package com.dianping.swallow.common.internal.exception;

/**
 * @author mengwenchao
 *
 * 2015年3月26日 下午2:24:10
 */
public class SwallowRuntimeException extends RuntimeException{

	private static final long serialVersionUID = 1L;

	public SwallowRuntimeException(String message){
		super(message);
	}

	public SwallowRuntimeException(String message, Throwable th){
		super(message, th);
	}


}
