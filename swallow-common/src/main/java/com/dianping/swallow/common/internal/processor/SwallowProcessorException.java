package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.exception.SwallowException;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午3:12:42
 */
public class SwallowProcessorException extends SwallowException{

	private static final long serialVersionUID = 1L;

	public SwallowProcessorException(String message) {
		super(message);
	}
	
	public SwallowProcessorException(String message, Throwable th){
		super(message, th);
	}
}
