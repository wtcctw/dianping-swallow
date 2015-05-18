package com.dianping.swallow;

import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:52:52
 */
public abstract class AbstractTest {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	
	
	protected String topicName = "SwallowUnitTest";
	
	protected String baseConsumerId = "ut";

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
	
	public static SwallowMessage createMessage() {

		SwallowMessage message = new SwallowMessage();
		message.setContent("this is a SwallowMessage");
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");
		return message;

	}

	protected String getConsumerId() {
		
		return baseConsumerId + "-" + testName.getMethodName();
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
