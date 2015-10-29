package com.dianping.swallow.consumer.internal.task;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

/**
 * @author mengwenchao
 *
 * 2015年3月30日 下午5:19:21
 */
public class LongTaskChecker implements TaskChecker, Runnable{
	
	private static final Logger logger = LogManager.getLogger(LongTaskChecker.class);

	private final String CAT_TYPE = "SwallowLongTask";

	private volatile boolean closed = false;
	
	/**
	 * 运行时间超过此时间，报警，单位毫秒
	 */
	private  int alertTime;
	
	private AtomicInteger alertCount = new AtomicInteger();
	
	private Map<Runnable, Long> tasks = new ConcurrentHashMap<Runnable, Long>();
	
	public LongTaskChecker(int alertTime){
		this.alertTime = alertTime;
	}
	
	@Override
	public void addTask(Runnable task){
		
		if(logger.isDebugEnabled()){
			logger.debug("[addTask]" + task);
		}
		tasks.put(task, System.currentTimeMillis());
	}
	
	@Override
	public void removeTask(Runnable task){
		
		if(logger.isDebugEnabled()){
			logger.debug("[removeTask]" + task);
		}
		tasks.remove(task);
	}
	
	@Override
	public void run() {

		if(alertTime <= 0){
			if(logger.isInfoEnabled()){
				logger.info("[run][exit alertTime <= 0]" + alertTime) ;
			}
			return;
		}
		
		while(!closed){
			try {
				TimeUnit.MILLISECONDS.sleep(alertTime);
			} catch (InterruptedException e) {
			}

			try{
				check();
			}catch(Throwable th){
				logger.error("[run]", th);
			}
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[run][exit]");
		}
	}

	private void check() {
		Long current = System.currentTimeMillis();
		
		for(Entry<Runnable, Long> taskEntry : tasks.entrySet()){
			Runnable task = taskEntry.getKey();
			Long 	startTime = taskEntry.getValue();

			if((current - startTime) > alertTime){
				alert(task);
			}
		}
	}

	private void alert(Runnable task) {
		
		if(logger.isInfoEnabled()){
			logger.info("[alert]" + task);
		}
		
		Transaction transaction = Cat.newTransaction(CAT_TYPE, task.toString() + "," + alertTime);
		transaction.setStatus(Transaction.SUCCESS);
		transaction.complete();
		
		alertCount.incrementAndGet();
	}

	@Override
	public void close() {
		closed = true;
	}

	@Override
	public int getAlertCount() {
		return alertCount.get();
	}
}
