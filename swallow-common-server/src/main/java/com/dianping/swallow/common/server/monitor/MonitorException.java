package com.dianping.swallow.common.server.monitor;

import com.dianping.swallow.common.internal.exception.SwallowRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年4月30日 下午2:27:05
 */
public class MonitorException extends SwallowRuntimeException{

	private static final long serialVersionUID = 1L;

	public MonitorException(String message) {
		super(message);
	}

}
