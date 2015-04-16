package com.dianping.swallow.test;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mengwenchao
 *
 * 2015年2月13日 下午1:29:16
 */
public class AbstractTest {
	
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected ExecutorService executors = Executors.newCachedThreadPool();


	@Rule
	public TestName  testName = new TestName();

	
	@Before
	public void beforeAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[beforeAbstractTest]" + testName.getMethodName());
		}
		
	}

	
	protected String createMessage(int size) {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<size;i++){
			sb.append("c");
		}
		return sb.toString();
	}
	
	
	@After
	public void afterAbstractTest(){
		if(logger.isInfoEnabled()){
			logger.info("[afterAbstractTest]" + testName.getMethodName());
		}
	}

	protected void sleep(int miliSeconds) {
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
