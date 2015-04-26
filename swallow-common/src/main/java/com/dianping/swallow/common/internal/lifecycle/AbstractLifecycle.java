package com.dianping.swallow.common.internal.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 生命周期抽象实现，只记录日志
 * @author mengwenchao
 *
 * 2014年11月7日 下午2:27:39
 */
public class AbstractLifecycle implements Lifecycle{

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
