package com.dianping.swallow.common.internal.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;

/**
 * 默认线程池
 * @author mengwenchao
 *
 * 2014年11月5日 下午5:47:19
 */
public class DefaultThreadProfile implements ThreadProfile{
	
	private final Logger logger = LogManager.getLogger(getClass());

	private final int cpuCount = Runtime.getRuntime().availableProcessors();
	
	private int corePoolSize = cpuCount;
	
	private int maxPoolSize = cpuCount * 2;

	private int keepAliveTime = 60;// seconds

	private String threadPoolName = "default_thread_pool";
	
	private RejectedExecutionHandler handler;
	
	private QueueFactory queueFactory;
	

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
		
		try {
			return new ThreadPoolExecutor(corePoolSize, 
						maxPoolSize, 
						keepAliveTime, TimeUnit.SECONDS,
						queueFactory!=null ? queueFactory.createQueue() : new LinkedBlockingQueue<Runnable>(),
						new MQThreadFactory(threadPoolName),
						handler == null ? new ThreadPoolExecutor.CallerRunsPolicy() : handler   
					);
		} catch (Exception e){
			logger.error("[createPool]", e);
		}
		
		return null;
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

	public QueueFactory getQueueFactory() {
		return queueFactory;
	}

	public void setQueueFactory(QueueFactory queueFactory) {
		this.queueFactory = queueFactory;
	}

}
