package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:07
 */
public abstract class AbstractAlarmer extends AbstractLifecycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private int alarmInterval = 30;

	private ScheduledFuture<?> future;

	private static final String FACTORY_NAME = "Alarmer";

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(
			CommonUtils.DEFAULT_CPU_COUNT * 2, ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	@Override
	protected void doStart() throws Exception {

		super.doStart();

		startAlarm();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();

		future.cancel(false);
	}

	public void startAlarm() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				try {
					doAlarm();

				} catch (Throwable th) {
					logger.error("[startAlarm]", th);
				} finally {

				}

			}

		}, getAlarmInterval(), getAlarmInterval(), TimeUnit.SECONDS);
	}

	public abstract void doAlarm();

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

}
