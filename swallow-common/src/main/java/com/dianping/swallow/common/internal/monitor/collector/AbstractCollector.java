package com.dianping.swallow.common.internal.monitor.collector;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.internal.lifecycle.AbstractLifecycle;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.CommonUtils;

/**
 * @author mengwenchao
 *
 * 2015年4月10日 下午2:08:18
 */
public abstract class AbstractCollector extends AbstractLifecycle implements Collector, Runnable{
	
	private ScheduledExecutorService scheduled;
	
	protected static final String THREAD_POOL_NAME = "Monitor-Collector-Thread-Pool";
	
	private int SEND_INTERVAL = 5;
	
	private ScheduledFuture<?> future; 
	
	@Override
	public void doInitialize() throws Exception {
		
		scheduled = Executors.newScheduledThreadPool(CommonUtils.getCpuCount(), new MQThreadFactory(THREAD_POOL_NAME)); 
	}
	
	@Override
	public void doStart() throws Exception {
		
		future = scheduled.scheduleAtFixedRate(this, SEND_INTERVAL, SEND_INTERVAL, TimeUnit.SECONDS);
	}
	

	@Override
	protected void doStop() throws Exception {
		future.cancel(false);
	}
	
	@Override
	public void doDispose() throws Exception {
		
		scheduled.shutdownNow();
	}
	
	@Override
	public void run() {
		
		try{
			doSendTask();
		}catch(Throwable th){
			logger.error("[run]", th);
		}
	}

	private void doSendTask() {
		
		
		
	}
}
