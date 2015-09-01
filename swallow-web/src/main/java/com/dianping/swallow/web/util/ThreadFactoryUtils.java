package com.dianping.swallow.web.util;

import java.util.concurrent.ThreadFactory;
/**
 * 
 * @author qiyin
 *
 * 2015年8月1日 下午12:47:01
 */
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
