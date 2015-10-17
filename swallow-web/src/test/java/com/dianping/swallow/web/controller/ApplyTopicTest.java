package com.dianping.swallow.web.controller;

import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;

/**
 * @author mingdongli
 *
 *         2015年10月15日下午6:03:24
 */
public class ApplyTopicTest {

	private String TOPIC = "lionapi-get-alpha-apply-topic";
	
	private static final int THREAD_SIZE = 5;
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		
		  CountDownLatch begSignal = new CountDownLatch(1);
		  CountDownLatch endSignal = new CountDownLatch(THREAD_SIZE);
		  
		  for (int i = 0; i < THREAD_SIZE; i++) {
		   new Thread(new Work(TOPIC + i, begSignal, endSignal)).start();
		  }
		  
		  try {
		   begSignal.countDown();
		   endSignal.await();    
		   System.out.println("运行结束");
		  } catch (InterruptedException e) {
		   e.printStackTrace();
		  }

	}

}
