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
		
		System.out.println(new Date(286610301L*5*1000));
		print("20150531135100");
		print("20150531135200");
		
		print("20150531144500");
		
		
	}

	/**
	 * @param time
	 * @throws ParseException 
	 */
	private void print(String timeStr) throws ParseException {
		System.out.println();
		long time = DateUtils.fromSimpleFormat(timeStr).getTime();
		System.out.println(time);
		System.out.println(time/5/1000);
		System.out.println(new Date(time));
	}

}
