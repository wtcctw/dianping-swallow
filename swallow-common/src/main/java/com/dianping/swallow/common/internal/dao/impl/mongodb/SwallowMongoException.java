package com.dianping.swallow.common.internal.dao.impl.mongodb;

import com.dianping.swallow.common.internal.exception.SwallowRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年3月26日 下午2:27:17
 */
public class SwallowMongoException extends SwallowRuntimeException{
	
	private static final long serialVersionUID = 1L;

	public SwallowMongoException(String message){
		super(message);
	}

	public SwallowMongoException(String message, Throwable th){
		super(message, th);
	}

}
