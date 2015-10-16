package com.dianping.swallow.web.alarmer.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

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

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		startAlarm();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}
	}

	protected void doDispose() throws Exception {
		super.doDispose();
	}

	public abstract void doAlarm();

	public void startAlarm() {
		future = threadManager.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, alarmName);
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doAlarm();
						}
					});

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
