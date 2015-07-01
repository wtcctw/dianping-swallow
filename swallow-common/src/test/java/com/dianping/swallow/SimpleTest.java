package com.dianping.swallow;

import java.text.ParseException;

import org.junit.Test;

import com.dianping.lion.client.ConfigCache;

/**
 * @author mengwenchao
 *
 * 2015年2月5日 下午4:16:13
 */
public class SimpleTest {

	
	@Test
	public void testLong() throws ParseException{
		
		System.out.println(getClass().getCanonicalName());
		System.out.println(getClass().getName());
		
		print("20150601165700");
		print("20150601145600");
		
	}
	
	@Test
	public void testLionConfig(){
		
		ConfigCache cc = ConfigCache.getInstance();
		String value = cc.getProperty("swallow.teSt");
		System.out.println(String.format("#%s#", value));
	}
	
	/**
	 * @param time
	 * @throws ParseException 
	 */
	private void print(String timeStr) throws ParseException {
		
	}

}
