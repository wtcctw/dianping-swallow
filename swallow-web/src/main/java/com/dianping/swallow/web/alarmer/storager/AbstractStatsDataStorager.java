package com.dianping.swallow.web.alarmer.storager;

import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.alarmer.TaskManager;
import com.dianping.swallow.web.monitor.MonitorDataListener;

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

	protected volatile long lastTimeKey = -1L;

	private Future<?> future;

	@Autowired
	protected TaskManager taskManager;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		storagerName = getClass().getSimpleName();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
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

	@Override
	public void achieveMonitorData() {
		future = taskManager.submit(new Runnable() {
			@Override
			public void run() {
				SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, storagerName + "-doStorage");
				catWrapper.doAction(new SwallowAction() {
					@Override
					public void doAction() throws SwallowException {
						doStorage();
					}
				});
			}
		});
	}

	protected abstract void doStorage();

	public long getLastTimeKey() {
		return lastTimeKey;
	}

}
