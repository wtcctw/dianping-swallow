package com.dianping.swallow.web.alarmer.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;


/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:07
 */
public abstract class AbstractAlarmer extends AbstractLifecycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected static final String FUNCTION_DOALARM = "-doAlarm";

}
