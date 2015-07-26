package com.dianping.swallow.web.util;

import java.util.concurrent.ThreadFactory;

public class ThreadFactoryUtils {
	
	public static ThreadFactory getThreadFactory(final String name){
		return 	new ThreadFactory(){

			@Override
			public Thread newThread(Runnable runnable) {
				return ThreadUtils.createThread(runnable, name, true);
			}
		};
	}

}
