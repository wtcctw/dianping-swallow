package com.dianping.swallow.test.load;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年1月28日 下午7:10:54
 */
public abstract class AbstractLoadTest {
	
    protected Logger logger       = LoggerFactory.getLogger(getClass());

	protected String topicName = "LoadTestTopic";
	
    protected  AtomicInteger count = new AtomicInteger();
    protected AtomicInteger preCount = new AtomicInteger();
    protected long preTime;
    protected long startTime;
    
    protected int zeroCount = 0;
    protected int zeroExit = 10;
    
    protected ScheduledExecutorService	scheduled = Executors.newScheduledThreadPool(4);
    protected ExecutorService executors = Executors.newCachedThreadPool();

    public static final int messageSize = 1000;
    public static final String message;
    

	static {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<messageSize;i++){
			sb.append("c");
		}
		message = sb.toString();
	}	

	
    protected String getTopicName(String name, int count) {
    	
    	
		return name + "-" + count;
	}
    
    
	protected void start() throws InterruptedException, IOException{
		
		startFrequencyCounter();
		
		startTime = System.currentTimeMillis();
		
		doStart();
		
		executors.shutdown();
		executors.awaitTermination(7, TimeUnit.DAYS);
		
		if(isExitOnExecutorsReturn()){
			if(logger.isInfoEnabled()){
				logger.info("[start][time exceed return]");
			}
			exit();
		}
		
	}
	private void exit() {
		
		logger.info("[exit]" + "Total Message count:" + count.get());
		logger.info("[exit]" + "Total Message Frequency:" + count.get()/((System.currentTimeMillis() - startTime)/1000));
		scheduled.shutdown();
		System.exit(0);
		
	}


	protected boolean isExitOnExecutorsReturn() {
		return true;
	}


	protected abstract void doStart() throws InterruptedException, IOException;

	private void startFrequencyCounter() {
		
        preTime = System.currentTimeMillis();
        preCount.set(0);
		
        final ScheduledFuture<?> future = scheduled.scheduleAtFixedRate(new Runnable(){
			@Override
			public void run() {
				
				try{
					
					long currentTime = System.currentTimeMillis();
					int currentCount = count.get();
					
					logger.info("[run]" + "current rate:" + (currentCount - preCount.get())/((currentTime - preTime)/1000));
					logger.info("[run]" + "total rate:" + (currentCount)/((currentTime - startTime)/1000));
					logger.info("[run]" + "message count:" + currentCount);
	
					if(currentCount - preCount.get() == 0){
						zeroCount++;
					}else{
						zeroCount = 0;
					}
					if(zeroCount >= zeroExit || (isExit())){
						logger.info("[run][zero size exceed maximum count, exit]" + zeroExit);
						exit();
					}
					preTime = currentTime;
					preCount.set(currentCount);
				}catch(Throwable th){
					logger.error("[run]", th);
				}
			}

        }, 5, 5, TimeUnit.SECONDS);
        
        
        executors.execute(new Runnable(){

			@Override
			public void run() {
				try {
					logger.info("[scheduled future]" + future.get());
				} catch (Exception e) {
					logger.error("[run]", e);
				}
			}
        	
        });
		
	}

	protected boolean isExit() {
		return false;
	}
}
