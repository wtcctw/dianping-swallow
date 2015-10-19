package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.server.Sendable;
import com.dianping.swallow.web.model.server.ServerFactoryImpl;
import com.dianping.swallow.web.service.IPCollectorService;

public abstract class AbstractServiceAlamer1 extends AbstractAlarmer {

	protected final static String CAT_TYPE = "ServiceAlarmer";

	private ScheduledFuture<?> future;

	@Autowired
	protected IPCollectorService ipCollectorService;

	@Autowired
	protected ResourceContainer resourceContainer;
	
	@Autowired
	protected ServerFactoryImpl serverFactory;

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

		}, 30, 30, TimeUnit.SECONDS);
	}

	public void doDataSend(final Sendable server, final String ip, final boolean isProducer) {
		try {
			long sendTimeStamp = ipCollectorService.getLastestStatsTimeByIp(ip, isProducer);
			server.checkSender(sendTimeStamp);
		} catch (Throwable t) {
			logger.error("[run] server {} checkSender error.", server);
		}
	}
}
