package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.TaskManager;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年10月15日 下午12:40:27
 */
@Component
public class TaskManagerImpl extends AbstractLifecycle implements TaskManager, AlarmerLifecycle {

	protected final static String EXECUTOR_FACTORY_NAME = "ExecutorAlamer";

	protected final static String SCHEDULED_FACTORY_NAME = "ScheduledAlamer";

	private ExecutorService executor = null;

	private ScheduledExecutorService scheduled = null;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 5,
				ThreadFactoryUtils.getThreadFactory(EXECUTOR_FACTORY_NAME));
		scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 2,
				ThreadFactoryUtils.getThreadFactory(SCHEDULED_FACTORY_NAME));
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		logger.info("[doStart] {} start.", getClass().getSimpleName());
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		logger.info("[doStart] {} stop.", getClass().getSimpleName());
	}

	protected void doDispose() throws Exception {
		super.doDispose();
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
		}
		if (scheduled != null && !scheduled.isShutdown()) {
			scheduled.shutdown();
		}
	}
	@Override
	public Future<?> submit(Runnable command) {
		return executor.submit(command);
	}

	@Override
	public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		return scheduled.scheduleAtFixedRate(command, initialDelay, period, unit);
	}

	@Override
	public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		return scheduled.scheduleWithFixedDelay(command, initialDelay, delay, unit);
	}

}
