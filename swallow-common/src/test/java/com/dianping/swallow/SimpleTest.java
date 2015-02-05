package com.dianping.swallow;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年2月5日 下午4:16:13
 */
public class SimpleTest {

	
	@Test
	public void testLong(){
		Long a = null;
		func(a);
		
	}

	/**
	 * @param a
	 */
	private void func(long a) {
		System.out.println(a);
	}
}
