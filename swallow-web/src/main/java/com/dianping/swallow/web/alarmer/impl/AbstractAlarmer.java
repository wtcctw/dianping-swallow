package com.dianping.swallow.web.alarmer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.alarmer.AlarmerLifeCycle;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:07
 */
public abstract class AbstractAlarmer extends AbstractLifecycle implements AlarmerLifeCycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static final String FUNCTION_DOALARM = "-doAlarm";

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		logger.info("[doStart] {} start.", getClass().getSimpleName());
	}
	
	@Override
	protected void doStop() throws Exception {
		super.doStop();
		logger.info("[doStart] {} stop.", getClass().getSimpleName());
	}

}
