package com.dianping.swallow.web.storager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
public abstract class AbstractStatsDataStorager extends AbstractLifecycle {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected volatile AtomicLong dataCount = new AtomicLong();

	protected String storagerName;

	private static final String FACTORY_NAME = "StatsStorager";

	protected static final int INIT_VALUE = 0;

	protected static final long DEFAULT_VALUE = -1L;

	protected volatile AtomicLong lastTimeKey = new AtomicLong();

	private int storagerInterval = 30;

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	private ScheduledFuture<?> future = null;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		dataCount.set(INIT_VALUE);
		lastTimeKey.set(DEFAULT_VALUE);
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		startStorage();
	}

	private void startStorage() {
		setFuture(scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper(storagerName, "doStorage");
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doStorage();
						}
					});
				} catch (Throwable th) {
					logger.error("[startStorage]", th);
				} finally {

				}
			}

		}, getStoragerInterval(), getStoragerInterval(), TimeUnit.SECONDS));
	}

	protected abstract void doStorage();

	public int getStoragerInterval() {
		return storagerInterval;
	}

	public void setStoragerInterval(int storagerInterval) {
		this.storagerInterval = storagerInterval;
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
