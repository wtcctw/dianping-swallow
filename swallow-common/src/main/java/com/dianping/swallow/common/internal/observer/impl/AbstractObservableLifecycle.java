package com.dianping.swallow.common.internal.observer.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.Lifecycle;

/**
 * @author mengwenchao
 *
 * 2015年6月11日 下午6:13:26
 */
public abstract class AbstractObservableLifecycle extends AbstractObservable implements Lifecycle{
	
	protected final Logger logger     = LoggerFactory.getLogger(getClass());

	@Override
	public void initialize() throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("[initialize]");
		}
		
		doInitialize();
		
	}

	protected void doInitialize() throws Exception {
		
	}

	@Override
	public void start() throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("[start]");
		}
		
		doStart();
	}

	protected void doStart() throws Exception {
		
	}

	@Override
	public void stop() throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("[stop]");
		}
		
		doStop();
	}

	protected void doStop() throws Exception {
		
	}

	@Override
	public void dispose() throws Exception {
		if(logger.isInfoEnabled()){
			logger.info("[dispose]");
		}
		
		doDispose();
	}

	protected void doDispose() throws Exception {
		
	}


}
