package com.dianping.swallow.common.internal.util;

import java.lang.Thread.UncaughtExceptionHandler;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultThreadExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultThreadExceptionHandler.class);

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

}
