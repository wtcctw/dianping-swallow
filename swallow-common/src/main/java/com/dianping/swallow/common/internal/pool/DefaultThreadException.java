package com.dianping.swallow.common.internal.pool;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class DefaultThreadException implements InitializingBean{
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultThreadException.class);

	public void setExceptionCaughtHandler(){
		
		if(logger.isInfoEnabled()){
			logger.info("[setExceptionCaughtHandler]");
		}

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				
				logger.error("uncaught exception in thread:" + t, e);
				
			}
		});
		
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setExceptionCaughtHandler();
	}

}
