package com.dianping.swallow.common.internal.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;

/**
 * 默认线程池
 * @author mengwenchao
 *
 * 2014年11月5日 下午5:47:19
 */
public class DefaultThreadProfile implements ThreadProfile{
	
	private final int cpuCount = Runtime.getRuntime().availableProcessors();
	
	private int corePoolSize = cpuCount;
	

	private int maxPoolSize = cpuCount * 2;

	private int keepAliveTime = 60;// seconds

	private String threadPoolName = "default_thread_pool";

	public DefaultThreadProfile(String threadPoolName){
		this.threadPoolName = threadPoolName;
	}
	
	@Override
	public int getCorePoolSize() {
		
		return corePoolSize;
	}

	@Override
	public int getMaxPoolSize() {

		return maxPoolSize;
	}

	@Override
	public ExecutorService createPool() {
		
		return new ThreadPoolExecutor(corePoolSize, 
					maxPoolSize, 
					keepAliveTime, TimeUnit.SECONDS, 
					new LinkedBlockingDeque<Runnable>(),
					new MQThreadFactory(threadPoolName),
					new ThreadPoolExecutor.CallerRunsPolicy()
				);
	}

	@Override
	public int getKeepAliveTime() {
		
		return keepAliveTime;
	}

	@Override
	public String getThreadPoolName() {

		return threadPoolName;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public void setMaxPoolSize(int maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public void setKeepAliveTime(int keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

}
