package com.dianping.swallow.common.internal.dao.impl.kafka;

import com.dianping.swallow.common.internal.exception.SwallowRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年11月8日 下午9:04:44
 */
public class SwallowKafkaException extends SwallowRuntimeException{

	private static final long serialVersionUID = -5578837899544251556L;

	public SwallowKafkaException(String message, Throwable th) {
		super(message, th);
	}

}
