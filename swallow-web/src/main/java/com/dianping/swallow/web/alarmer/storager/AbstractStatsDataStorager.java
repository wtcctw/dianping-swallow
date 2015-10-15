package com.dianping.swallow.web.alarmer.storager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
public abstract class AbstractStatsDataStorager extends AbstractLifecycle implements MonitorDataListener,
		StoragerLifecycle {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String storagerName;

	protected final static String CAT_TYPE = "StatsDataStorager";

	private static final String FACTORY_NAME = "StatsDataStorager";

	protected volatile long lastTimeKey = -1L;

	protected static ExecutorService executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 2,
			ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {

	}

	protected void doDispose() throws Exception {
		super.doDispose();
		if (executor != null && !executor.isShutdown()) {
			executor.shutdown();
		}
	}

	@Override
	public void achieveMonitorData() {
		executor.submit(new Runnable() {

			@Override
			public void run() {
				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, storagerName + "-doStorage");
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
		});
	}

	protected abstract void doStorage();

	public long getLastTimeKey() {
		return lastTimeKey;
	}

}
