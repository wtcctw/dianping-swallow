package com.dianping.swallow;

import java.text.ParseException;
import java.util.Date;

import org.junit.Test;

import com.dianping.swallow.common.internal.util.DateUtils;

/**
 * @author mengwenchao
 *
 * 2015年2月5日 下午4:16:13
 */
public class SimpleTest {

	
	@Test
	public void testLong() throws ParseException{
		
		print("20150530153030");
		print("20150530093430");
		
		
	}

	/**
	 * @param time
	 * @throws ParseException 
	 */
	private void print(String timeStr) throws ParseException {
		
		long time = DateUtils.fromSimpleFormat(timeStr).getTime();
		System.out.println(time);
		System.out.println(new Date(time));
	}

}
