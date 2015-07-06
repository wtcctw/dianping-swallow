package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.alarmer.Alarmer;

/**
*
* @author qiyin
*
*/
public abstract class AbstractAlarmer extends AbstractLifecycle implements Alarmer {
	
	private static final Logger logger = LoggerFactory.getLogger(AbstractAlarmer.class);

	protected final int DEFAULT_INTERVAL = 5;
	
	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT);
	
	private ScheduledFuture<?> future = null;
	@Override
	protected void doStart() throws Exception{
		super.doStart();
		startAlarmer();
	}
	
	private void startAlarmer(){
		future = scheduled.scheduleAtFixedRate(new Runnable(){
			
			@Override
			public void run(){
				try{
					doAlarm();
				}catch(Throwable th){
					logger.error("[startAlarmer]",th);
				}finally{
					
				}
			}
			
		}, getAlarmInterval(), getAlarmInterval(), TimeUnit.SECONDS);
	}
	
	@Override
	protected void doStop() throws Exception{
		super.doStop();
		future.cancel(true);
	}
	
	protected abstract void doAlarm();
	
	protected int getAlarmInterval(){
		return DEFAULT_INTERVAL;
	}
	
}
