package com.dianping.swallow;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:52:52
 */
public abstract class AbstractTest {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

	@Rule
	public TestName  testName = new TestName();

	@Before
	public void beforeAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[-----------------][begin test]" + testName.getMethodName());
		}
	}

	
	protected void sleep(int miliSeconds){
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			logger.error("[sleep]", e);
		}
	}
	
	
	@After
	public void afterAbstractTest(){
		
		if(logger.isInfoEnabled()){
			logger.info("[-----------------][end test]" + testName.getMethodName());
		}
		
		executors.shutdownNow();
		scheduledExecutors.shutdownNow();
	}

}
