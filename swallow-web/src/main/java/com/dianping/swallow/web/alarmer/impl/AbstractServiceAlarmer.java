package com.dianping.swallow.web.alarmer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:14
 */
public abstract class AbstractServiceAlarmer extends AbstractAlarmer {

	protected final static String CAT_TYPE = "ServiceAlarmer";

	protected Map<String, Boolean> lastCheckStatus = new HashMap<String, Boolean>();

	@Autowired
	private HttpService httpService;

	@Autowired
	protected EventReporter eventReporter;

	@Autowired
	protected EventFactory eventFactory;

	private ScheduledFuture<?> future;

	protected int alarmInterval = 30;

	protected int alarmDelay = 30;

	private static final String FACTORY_NAME = "ServiceAlarmer";

	private static ScheduledExecutorService scheduled = null;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 2,
				ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
	}

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

	public abstract void doAlarm();

	public void startAlarm() {
		if (scheduled == null) {
			return;
		}
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

		}, getAlarmDelay(), getAlarmInterval(), TimeUnit.SECONDS);
	}

	public int getAlarmInterval() {
		return alarmInterval;
	}

	public void setAlarmInterval(int alarmInterval) {
		this.alarmInterval = alarmInterval;
	}

	public int getAlarmDelay() {
		return alarmDelay;
	}

	public void setAlarmDelay(int alarmDelay) {
		this.alarmDelay = alarmDelay;
	}

	protected void threadSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logger.error("[threadSleep] interrupted.", e);
		}
	}

	protected HttpResult httpRequest(String url) {
		HttpResult result = httpService.httpGet(url);
		if (!result.isSuccess()) {
			threadSleep();
			result = httpService.httpGet(url);
		}
		return result;
	}

}
