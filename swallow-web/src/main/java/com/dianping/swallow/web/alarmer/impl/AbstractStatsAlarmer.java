package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:20
 */
public abstract class AbstractStatsAlarmer extends AbstractAlarmer implements MonitorDataListener {

	protected final static String CAT_TYPE = "StatsDataAlarmer";

	protected static final String FACTORY_NAME = "StatsDataAlarmer";

	protected static final String TOTAL_KEY = MonitorData.TOTAL_KEY;

	private final static long DAY_TIMESTAMP_UNIT = 24 * 60 * 60 * 1000;

	private static ExecutorService executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 4,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	protected static final long TIME_SECTION = 5 * 60 / AbstractCollector.SEND_INTERVAL;

	private long lastTimeKey = -1;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		executor.shutdown();
	}

	@Override
	public void achieveMonitorData() {
		logger.info("[achieveMonitorData] statsDataAlarmer {}", getClass().getSimpleName());

		executor.submit(new Runnable() {

			@Override
			public void run() {
				doAlarm();
			}

		});
	}

	public abstract void doAlarm();

	public long getLastTimeKey() {
		return lastTimeKey;
	}

	public void setLastTimeKey(long lastTimeKey) {
		this.lastTimeKey = lastTimeKey;
	}

	protected long getPreNDayKey(int n, long timespan) {
		return System.currentTimeMillis() - n * DAY_TIMESTAMP_UNIT - timespan;
	}

	protected long getTimeKey(long timeMillis) {
		return AbstractRetriever.getKey(timeMillis);
	}

	protected static long getPreDayKey(long timeKey) {
		return timeKey - AbstractRetriever.getKey(DAY_TIMESTAMP_UNIT).longValue();
	}

	protected static final long getTimeSection() {
		return TIME_SECTION;
	}

}
