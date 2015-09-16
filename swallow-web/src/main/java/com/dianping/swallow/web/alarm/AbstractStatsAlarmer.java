package com.dianping.swallow.web.alarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月14日 下午3:40:54
 */
public abstract class AbstractStatsAlarmer extends AbstractAlarmer implements MonitorDataListener {

	protected final static String CAT_TYPE = "StatsDataAlarmer";

	protected String FACTORY_NAME = "StatsDataAlarmer";

	private final static long DAY_TIMESTAMP_UNIT = 24 * 60 * 60 * 1000;

	private ExecutorService executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 4,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	private long lastTimeKey = -1;

	@Override
	public void achieveMonitorData() {
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

}
