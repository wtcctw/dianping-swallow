package com.dianping.swallow.web.monitor.collector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

public abstract class AbstractResourceCollector extends AbstractLifecycle implements CollectorLifeCycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected final static String CAT_TYPE = "ResourceCollector";

	private static final String FACTORY_NAME = "ResourceCollector";

	private ScheduledFuture<?> future;

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	protected String collectorName;
	
	protected int collectorInterval;

	protected int collectorDelay;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		startCollector();
	}

	public abstract void doCollector();

	public abstract int getCollectorDelay();

	public abstract int getCollectorInterval();

	public void startCollector() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {

				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, collectorName + "-doCollector");
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doCollector();
						}
					});
				} catch (Throwable th) {
					logger.error("[startAlarm]", th);
				} finally {

				}

			}

		}, getCollectorDelay(), getCollectorInterval(), TimeUnit.SECONDS);
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
