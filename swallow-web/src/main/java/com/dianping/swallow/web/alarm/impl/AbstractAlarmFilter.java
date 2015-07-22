package com.dianping.swallow.web.alarm.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.web.alarm.AlarmFilter;
import com.dianping.swallow.web.alarm.AlarmFilterChain;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;

/**
 *
 * @author qiyin
 *
 */
public abstract class AbstractAlarmFilter implements AlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAlarmFilter.class);

	@Override
	public boolean accept(AlarmFilterChain alarmFilterChain) {
		try {
			doAccept();
		} catch (Exception e) {
			logger.error("[accept] doAccept has exception. ", e);
		}
		return alarmFilterChain.doNext();
	}

	public abstract boolean doAccept();

	protected static final long TIME_SECTION = 5 * 60 / 5;

	protected static long getPreDayKey(long timeKey) {
		return timeKey - AbstractRetriever.getKey(24 * 60 * 60 * 1000).longValue();
	}

	protected static final long getTimeSection() {
		return TIME_SECTION;
	}

}
