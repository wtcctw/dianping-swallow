package com.dianping.swallow;

import java.io.IOException;
import java.net.Socket;
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
import org.mockito.MockitoAnnotations;

import com.dianping.swallow.common.internal.message.InternalProperties;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.TestSkipRule;

/**
 * @author mengwenchao
 *
 * 2015年2月4日 下午4:52:52
 */
public abstract class AbstractTest {
	
	protected Logger logger = Logger.getLogger(getClass());
	
	private final int localWebPort = 8080;
	
	protected ExecutorService executors = Executors.newCachedThreadPool();

	protected ScheduledExecutorService scheduledExecutors = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
	
	protected String topicName = "SwallowUnitTest";
	
	protected String baseConsumerId = "ut";

	@Rule
	public TestName  testName = new TestName();
	
	@Rule
	public TestSkipRule testSkipRule = new TestSkipRule();

	@Before
	public void beforeAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[-----------------][begin test]" + testName.getMethodName());
		}
		
		System.setProperty("lion.useLocal", "true");
		MockitoAnnotations.initMocks(this);
				
	}

	
	protected void sleep(int miliSeconds){
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			logger.error("[sleep]", e);
		}
	}

	public SwallowMessage createMessage() {

		SwallowMessage message = new SwallowMessage();
		message.setMessageId(System.currentTimeMillis());
		message.setContent("this is a SwallowMessage");
		message.setGeneratedTime(new Date());
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("property-key", "property-value");
		message.setProperties(map);
		message.setSha1("sha-1 string");
		message.setVersion("0.6.0");
		message.setType("feed");
		message.setSourceIp("localhost");
		
		message.putInternalProperty(InternalProperties.SAVE_TIME, String.valueOf(System.currentTimeMillis() - 50));
		return message;
	}

	protected boolean testLocalWebServer() {
		
		Socket s = null;
		
		try {
			s = new Socket("127.0.0.1", localWebPort);
		} catch (Exception e) {
			logger.error("[testLocalWebServer]", e);
			return false;
		}finally{
			if(s != null){
				try {
					s.close();
				} catch (IOException e) {
					logger.error("[testLocalWebServer][close]", e);
				}
			}
		}
		return true;
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
