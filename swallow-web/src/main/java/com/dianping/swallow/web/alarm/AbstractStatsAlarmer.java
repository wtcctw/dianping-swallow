package com.dianping.swallow.web.alarm;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月14日 下午3:40:54
 */
public abstract class AbstractStatsAlarmer extends AbstractAlarmer implements MonitorDataListener {

	protected final static String FACTORY_NAME = "StatsDataAlarmer";

	protected static ExecutorService executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 4,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	private long lastTimeKey = -1;

	@Override
	public void achieveMonitorData() {
		doAlarm();
	}

	public abstract void doAlarm();

	public long getLastTimeKey() {
		return lastTimeKey;
	}

	public void setLastTimeKey(long lastTimeKey) {
		this.lastTimeKey = lastTimeKey;
	}
}
