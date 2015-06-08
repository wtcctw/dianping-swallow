package com.dianping.swallow.test.other;

import java.util.Date;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年5月16日 上午11:40:37
 */
public class SimpleTester {

	@Test
	public void testDate(){
		
//		printTime(286349333);
		
		printTime(286700179);
		
	}

	private void printTime(int time) {
		
		System.out.println(new Date((long)time * 5 * 1000));
		
	}
}
