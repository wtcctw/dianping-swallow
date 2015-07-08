package com.dianping.swallow.common.server.monitor.data.statis;

import com.dianping.swallow.common.server.monitor.MonitorException;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午3:01:04
 */
public class UnfoundKeyException extends MonitorException{

	private static final long serialVersionUID = 1L;

	public UnfoundKeyException(String message) {
		super(message);
	}

}
