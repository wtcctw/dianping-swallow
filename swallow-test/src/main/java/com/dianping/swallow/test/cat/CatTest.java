package com.dianping.swallow.test.cat;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.exception.SwallowAlertException;
import com.dianping.swallow.test.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年3月20日 下午3:45:01
 */
public class CatTest extends AbstractTest{

	
	@Test
	public void testArray(){
		Long []data = new Long[5];
		for(Long i : data){
			System.out.println(i);
		}
	}
	
	@Test
	public void testTransaction(){
		
		for(int i=0;i<100;i++){
			
			Transaction t = Cat.newTransaction("test", "test");
			try{
				if((i&1) == 0){
					t.setStatus(Transaction.SUCCESS);
				}else{
					t.setStatus(new Exception("man made exception!!"));
				}
			}finally{
				t.complete();
			}
			sleep(1);
		}
	}
	
	@Test
	public void testGetTime(){
		long date = 6130108015858157738L;
		System.out.println(new Date((date >> 32) *1000));
		
	}
	
	@Test
	public void testException() throws InterruptedException{
		
		for(int i=0; i < 5;i++){
			
			logger.error("message", new SwallowAlertException("exception message"));
			TimeUnit.SECONDS.sleep(1);
		}
	}
}
