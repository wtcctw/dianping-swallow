package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.atomic.AtomicLong;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:20
 */
public abstract class AbstractStatsAlarmer extends AbstractAlarmer {

	protected volatile AtomicLong dataCount = new AtomicLong();
	
	protected static final String TOTAL_KEY = "total";

	protected static final int INIT_VALUE = 0;
	protected static final long DEFAULT_VALUE = -1L;

	protected volatile AtomicLong lastTimeKey = new AtomicLong();

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		dataCount.set(INIT_VALUE);
		lastTimeKey.set(DEFAULT_VALUE);
	}

	protected static final long TIME_SECTION = 5 * 60 / AbstractCollector.SEND_INTERVAL;

	protected static long getPreDayKey(long timeKey) {
		return timeKey - AbstractRetriever.getKey(24 * 60 * 60 * 1000).longValue();
	}

	protected static final long getTimeSection() {
		return TIME_SECTION;
	}

}
