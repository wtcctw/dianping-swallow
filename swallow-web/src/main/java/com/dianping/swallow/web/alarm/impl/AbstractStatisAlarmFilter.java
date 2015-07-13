package com.dianping.swallow.web.alarm.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.dianping.swallow.web.alarm.AlarmFilter;

/**
 *
 * @author qiyin
 *
 */
public abstract class AbstractStatisAlarmFilter extends AbstractAlarmFilter implements AlarmFilter {

	protected volatile AtomicLong dataCount = new AtomicLong();

	protected static final int INIT_VALUE = 0;
	protected static final long DEFAULT_VALUE = -1L;

	protected volatile AtomicLong lastTimeKey = new AtomicLong();

	protected void initialize() {
		dataCount.set(INIT_VALUE);
		lastTimeKey.set(DEFAULT_VALUE);
	}

}
