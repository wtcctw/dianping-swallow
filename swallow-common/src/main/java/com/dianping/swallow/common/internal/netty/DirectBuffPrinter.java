package com.dianping.swallow.common.internal.netty;


import java.lang.reflect.Field;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;

/**
 * @author mengwenchao
 * 
 *         2015年8月27日 下午2:31:48
 */
public class DirectBuffPrinter extends AbstractLifecycle implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(1);
	
	private Class<?> nioClass;
	private Field maxMemory;
	private Field reservedMemory;
	
	private ScheduledFuture<?> future; 
	
	public DirectBuffPrinter(){
		
		try {
			
			nioClass = Class.forName("java.nio.Bits");
			maxMemory = nioClass.getDeclaredField("maxMemory");
			maxMemory.setAccessible(true);
			reservedMemory = nioClass.getDeclaredField("reservedMemory");
			reservedMemory.setAccessible(true);
		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	

	@Override
	public void doStart(){
		
		if(logger.isInfoEnabled()){
			logger.info("[start][Max]" + maxMemory());
		}
		
		future = scheduled.scheduleAtFixedRate(this, 0, 5, TimeUnit.SECONDS);
	}
	
	@Override
	protected void doStop() throws Exception {
		
		if(future != null){
			future.cancel(false);
		}
	}
	
	

	public Long maxMemory() {
		
		try {
			return (Long) maxMemory.get(null);
		} catch (Exception e){
			return 0L;
		}		
	}
	
	private Long reservedMemory(){
		
		try {
			return (Long) reservedMemory.get(null);
		} catch (Exception e){
			return 0L;
		}		
	}

	private String getReadable(Long size) {

		return size / 1024 + "/KB," + size / 1024 / 1024 + "/MB";
	}

	@Override
	public void run() {
		
		if(logger.isDebugEnabled()){
			logger.debug("[run][Used]" +  getReadable(reservedMemory()));
		}
	}
	
	@Override
	public Object getStatus() {
		return getReadable(reservedMemory());
	}
}
