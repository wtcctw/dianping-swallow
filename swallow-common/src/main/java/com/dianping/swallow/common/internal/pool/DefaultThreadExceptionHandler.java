package com.dianping.swallow.common.internal.pool;

import java.lang.Thread.UncaughtExceptionHandler;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class DefaultThreadExceptionHandler{
	
	private static final Logger logger = LogManager.getLogger(DefaultThreadExceptionHandler.class);
	
	
	static{
		setExceptionCaughtHandler();
	}

	private static void setExceptionCaughtHandler(){
		
		if(logger.isInfoEnabled()){
			logger.info("[setExceptionCaughtHandler]");
		}

		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				
				e.printStackTrace();
				logger.error("uncaught exception in thread:" + t, e);
				
			}
		});
		
	}

}
