package com.dianping.swallow.common.internal.pool;

import java.util.concurrent.ExecutorService;

/**
 * 线程池配置
 * 
 * @author mengwenchao
 *
 * 2014年11月5日 下午5:43:38
 */
public interface ThreadProfile {
	
	String getThreadPoolName();
	
	int getCorePoolSize();

	int getMaxPoolSize();
	
	int getKeepAliveTime();

	ExecutorService createPool();
}
