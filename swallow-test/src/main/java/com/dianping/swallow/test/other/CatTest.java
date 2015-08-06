package com.dianping.swallow.test.other;

import org.junit.Test;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.test.AbstractTest;

/**
 * @author mengwenchao
 * 
 *         2015年4月10日 下午2:21:04
 */
public class CatTest extends AbstractTest {
	
	
	@Test
	public void testLong(){
		System.out.println(Long.MAX_VALUE/5000000/86400/365);
	}

	@Test
	public void testLogError() {

		
		for (int i = 0; i < 5; i++) {
			
			newTransaction(i);
			Cat.logError(new Exception("log error"));
			logger.error("message", new Exception("just test"));
			sleep(1000);
			
		}
	}

	protected void newTransaction(int i) {

		Transaction t = Cat.newTransaction("CatTest", String.valueOf(i));
		t.setStatus(Transaction.SUCCESS);
		t.complete();
	}

}
