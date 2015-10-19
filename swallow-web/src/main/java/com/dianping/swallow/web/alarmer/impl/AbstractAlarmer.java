package com.dianping.swallow.web.alarmer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.TaskManager;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:07
 */
public abstract class AbstractAlarmer extends AbstractLifecycle implements AlarmerLifecycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static final String DOALARM_FUNCTION = "-doAlarm";

	@Autowired
	protected TaskManager taskManager;

	protected String alarmName;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmName = getClass().getSimpleName() + DOALARM_FUNCTION;
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
