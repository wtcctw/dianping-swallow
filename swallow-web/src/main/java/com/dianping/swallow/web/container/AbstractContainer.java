package com.dianping.swallow.web.container;

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
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年10月9日 上午10:29:54
 */
public abstract class AbstractContainer extends AbstractLifecycle {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected final static String CAT_TYPE = "DataContainer";

	private ScheduledFuture<?> future = null;

	private static final String FACTORY_NAME = "DataContainer";

	protected String containerName;

	protected int interval;

	protected int delay;

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		startLoadResource();
	}

	private void startLoadResource() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, containerName + "-doLoadResource");
				catWrapper.doAction(new SwallowAction() {
					@Override
					public void doAction() throws SwallowException {
						doLoadResource();
					}
				});

			}

		}, getDelay(), getInterval(), TimeUnit.SECONDS);
	}

	public abstract void doLoadResource();

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}

	}

	protected void doDispose() throws Exception {
		super.doDispose();
		if (scheduled != null && !scheduled.isShutdown()) {
			scheduled.shutdown();
		}
	}

	public int getInterval() {
		return interval;
	}

	public int getDelay() {
		return interval;
	}

}
