package com.dianping.swallow.common.internal.monitor.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.monitor.ComponentMonitable;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年6月26日 下午4:31:54
 */
public abstract class AbstractComponentMonitorable implements ComponentMonitable{
	
	@JsonIgnore
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	@Override
	public String getName() {
		
		return getClass().getName();
	}

	@Override
	public Object getStatus() {
		return this.toString();
	}
}
