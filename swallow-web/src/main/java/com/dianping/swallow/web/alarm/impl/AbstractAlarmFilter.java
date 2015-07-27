package com.dianping.swallow.web.alarm.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarm.AlarmFilter;
import com.dianping.swallow.web.alarm.AlarmFilterChain;
import com.dianping.swallow.web.alarm.EventReporter;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;

/**
 *
 * @author qiyin
 *
 */
public abstract class AbstractAlarmFilter implements AlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(AbstractAlarmFilter.class);
	
	@Autowired
	protected EventReporter eventReporter;

	@Override
	public boolean accept(AlarmFilterChain alarmFilterChain) {
		try {
			SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAccept");
			catWrapper.doAction(new SwallowAction() {
				@Override
				public void doAction() throws SwallowException {
					doAccept();
				}
			});

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
