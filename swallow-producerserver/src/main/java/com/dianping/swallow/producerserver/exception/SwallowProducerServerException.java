package com.dianping.swallow.producerserver.exception;

import com.dianping.swallow.common.internal.exception.SwallowException;

/**
 * @author mengwenchao
 *
 * 2015年10月30日 下午2:00:28
 */
public class SwallowProducerServerException extends SwallowException{

	private static final long serialVersionUID = 1L;
	
	
	public SwallowProducerServerException(String message){
		super(message);
	}

	public SwallowProducerServerException(String message, Throwable th){
		super(message, th);
	}


}
