package com.dianping.swallow.consumer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;

/**
 * @author mengwenchao
 *
 * 2015年3月10日 下午6:20:01
 */
public class CatTest {
	
	private ExecutorService executors = Executors.newCachedThreadPool();
	
	@Test
	public void testMultiProducer() throws InterruptedException{

		for(int i=0;i<1000;i++){
			
			executors.execute(new Runnable(){
	
				@Override
				public void run() {
			        Transaction consumerClientTransaction = Cat.getProducer().newTransaction("MsgConsumed", "test");
			        consumerClientTransaction.complete();
				}
				
			});
		}
		
		TimeUnit.SECONDS.sleep(10);
	}

}
