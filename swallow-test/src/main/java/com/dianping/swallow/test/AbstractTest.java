package com.dianping.swallow.test;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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


	protected void sleep(int miliSeconds) {
		
		try {
			TimeUnit.MILLISECONDS.sleep(miliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
