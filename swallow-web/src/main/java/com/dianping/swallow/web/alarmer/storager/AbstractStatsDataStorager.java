package com.dianping.swallow.web.alarmer.storager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
public abstract class AbstractStatsDataStorager extends AbstractLifecycle implements MonitorDataListener {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected String storagerName;
	
	protected final static String CAT_TYPE = "StatsStorager";

	private static final String FACTORY_NAME = "StatsStorager";

	protected static final long DEFAULT_VALUE = -1L;

	protected volatile AtomicLong lastTimeKey = new AtomicLong();

	protected ExecutorService scheduled = Executors.newSingleThreadExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		lastTimeKey.set(DEFAULT_VALUE);
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	public void achieveMonitorData() {
		scheduled.submit(new Runnable() {

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
		});
	}

	protected abstract void doStorage();

}
