package com.dianping.swallow.common.internal.lifecycle.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;
import com.dianping.swallow.common.internal.lifecycle.LifecycleCallback;
import com.dianping.swallow.common.internal.lifecycle.LifecycleManager;
import com.dianping.swallow.common.internal.lifecycle.Ordered;
import com.dianping.swallow.common.internal.monitor.LifecycleComponentStatus;
import com.dianping.swallow.common.internal.monitor.impl.AbstractComponentMonitorable;

/**
 * 生命周期抽象实现，只记录日志
 * @author mengwenchao
 *
 * 2014年11月7日 下午2:27:39
 */
public abstract class AbstractLifecycle extends AbstractComponentMonitorable implements Lifecycle, LifecycleComponentStatus{

	protected final Logger logger     = LoggerFactory.getLogger(getClass());
	
	private LifecycleManager lifecycleManager = new DefaultLifecycleManager(this);

	@Override
	public void initialize() throws Exception {
		
		lifecycleManager.initialize(new LifecycleCallback() {
			
			@Override
			public void onTransition() throws Exception {
				doInitialize();
			}
		});
	}

	protected void doInitialize() throws Exception {
		
	}

	@Override
	public void start() throws Exception {

		lifecycleManager.start(new LifecycleCallback() {
			
			@Override
			public void onTransition() throws Exception {
				
				doStart();
			}
		});
		
	}

	protected void doStart() throws Exception {
		
	}

	@Override
	public void stop() throws Exception {
		lifecycleManager.stop(new LifecycleCallback() {
			
			@Override
			public void onTransition() throws Exception {
				doStop();
			}
		});
		
	}

	protected void doStop() throws Exception {
		
	}

	@Override
	public void dispose() throws Exception {
		lifecycleManager.dispose(new LifecycleCallback() {
			
			@Override
			public void onTransition() throws Exception {
				doDispose();
			}
		});
	}

	protected void doDispose() throws Exception {
		
	}

	@Override
	public int getOrder() {
		
		return Ordered.LAST;
	}

	@Override
	public String getLifecyclePhase() {
		return lifecycleManager.getCurrentPhaseName();
	}
}
